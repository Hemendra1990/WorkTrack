#include <jni.h>
#include "com_hemendra_activity_systemevent_libs_ScreenLockDetector.h"
#include <CoreFoundation/CoreFoundation.h>
#include <IOKit/IOKitLib.h>
#include <IOKit/pwr_mgt/IOPMLib.h>
#include <IOKit/IOMessage.h>

static io_connect_t root_port;
static IONotificationPortRef notifyPortRef;
static io_object_t notifierObject;
static jobject globalObject = NULL;
static jmethodID onScreenLockedMethod = NULL;
static jmethodID onScreenUnlockedMethod = NULL;

void screenStateChanged(void *refCon, io_service_t service, natural_t messageType, void *messageArgument) {
    JNIEnv *env;
    JavaVM *jvm = (JavaVM *)refCon;
    jvm->AttachCurrentThread((void **)&env, NULL);

    switch (messageType) {
        case kIOMessageSystemWillSleep:
            env->CallVoidMethod(globalObject, onScreenLockedMethod);
            break;
        case kIOMessageSystemHasPoweredOn:
            env->CallVoidMethod(globalObject, onScreenUnlockedMethod);
            break;
    }

    jvm->DetachCurrentThread();
}

extern "C" JNIEXPORT void JNICALL Java_com_example_ScreenLockDetector_startScreenLockDetection
  (JNIEnv* env, jobject obj) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    globalObject = env->NewGlobalRef(obj);
    jclass clazz = env->GetObjectClass(obj);
    onScreenLockedMethod = env->GetMethodID(clazz, "onScreenLocked", "()V");
    onScreenUnlockedMethod = env->GetMethodID(clazz, "onScreenUnlocked", "()V");

    root_port = IORegisterForSystemPower(jvm, &notifyPortRef, screenStateChanged, &notifierObject);
    if (root_port == 0) {
        printf("IORegisterForSystemPower failed\n");
        return;
    }

    CFRunLoopAddSource(CFRunLoopGetCurrent(),
                       IONotificationPortGetRunLoopSource(notifyPortRef),
                       kCFRunLoopDefaultMode);
}

extern "C" JNIEXPORT void JNICALL Java_com_example_ScreenLockDetector_stopScreenLockDetection
  (JNIEnv* env, jobject obj) {
    IODeregisterForSystemPower(&notifierObject);
    IOServiceClose(root_port);
    IONotificationPortDestroy(notifyPortRef);

    if (globalObject != NULL) {
        env->DeleteGlobalRef(globalObject);
        globalObject = NULL;
    }
}
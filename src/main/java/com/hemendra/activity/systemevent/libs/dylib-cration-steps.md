# Steps to create dynamic linking library

## 1. Create a Java class

```bash
public class ScreenLockDetector {
    static {
        System.loadLibrary("screenlockdetector");
    }

    public native void startScreenLockDetection();
    public native void stopScreenLockDetection();

    private void onScreenLocked() {
        // Handle screen locked event
        System.out.println("Screen locked");
    }

    private void onScreenUnlocked() {
        // Handle screen unlocked event
        System.out.println("Screen unlocked");
    }
}
```
The below code uses the dynamic linking library (dylib) which is not created yet

```
static {
        #screenlockdetector.dylib (is the library we will be creating)
        System.loadLibrary("screenlockdetector");
    }
```

## 2. Compile the java class
```
javac -h . ScreenLockDetector.java
```
#### - this will generate a header file with the name similar to the package name as following
```
com_hemendra_activity_systemevent_libs_ScreenLockDetector.h
```

## 3. Create a CPP(C++) header file and write the code which you intendto do with the maching
```
# file-name: screenlockdetector.cpp

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
```

## 4. Compile the header file
```
g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" -shared -fPIC -framework IOKit -framework CoreFoundation -o libscreenlockdetector.dylib screenlockdetector.cpp
```


## Additional Commands to verify that everything is working as expected
```
```
The header file you provided matches the package and method names correctly for JNI. Now, let's go through the steps to ensure everything works as expected, given the context of your setup.

### Steps to Fix the `UnsatisfiedLinkError`:

1. **Check Native Method Implementation**:
    - Ensure the native methods `startScreenLockDetection` and `stopScreenLockDetection` are correctly defined in your C++ source file (`screenlockdetector.cpp`). The function signatures in your `.cpp` file should match the header file exactly:

   ```cpp
   #include "com_hemendra_activity_systemevent_libs_ScreenLockDetector.h"
   #include <iostream>
   #include <CoreFoundation/CoreFoundation.h>
   #include <IOKit/IOKitLib.h>
   #include <IOKit/pwr_mgt/IOPMLib.h>
   #include <IOKit/IOMessage.h>

   // Implement startScreenLockDetection
   JNIEXPORT void JNICALL Java_com_hemendra_activity_systemevent_libs_ScreenLockDetector_startScreenLockDetection
   (JNIEnv *env, jobject obj) {
       // Your logic for starting screen lock detection goes here
       std::cout << "Screen lock detection started." << std::endl;
   }

   // Implement stopScreenLockDetection
   JNIEXPORT void JNICALL Java_com_hemendra_activity_systemevent_libs_ScreenLockDetector_stopScreenLockDetection
   (JNIEnv *env, jobject obj) {
       // Your logic for stopping screen lock detection goes here
       std::cout << "Screen lock detection stopped." << std::endl;
   }
   ```

   Make sure that:
    - The method names exactly match the ones generated in the header file.
    - The method parameters (`JNIEnv *env, jobject obj`) are correctly implemented.

2. **Compile the Native Code**:
   After ensuring the method implementations are correct, recompile your native library. Make sure the compilation command includes the correct paths to Java headers, and links to the required macOS frameworks:

   ```bash
   g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" -shared -fPIC -framework IOKit -framework CoreFoundation -o screenlockdetector.dylib screenlockdetector.cpp
   ```

    - Ensure that `$JAVA_HOME` points to the correct Java installation.
    - If the library is not successfully compiled, check for errors and resolve them before proceeding.

3. **Ensure the Native Library is Loaded Properly**:
    - In your `ScreenLockDetector` Java class, ensure the native library is being loaded correctly. Based on the snippet you provided, it seems you already have a fallback mechanism to load the library from a temporary file if the default `System.loadLibrary` fails.

   The class should look like this:
   ```java
   package com.hemendra.activity.systemevent.libs;

   public class ScreenLockDetector {

       static {
           try {
               System.loadLibrary("screenlockdetector");
           } catch (UnsatisfiedLinkError e) {
               String libPath = "/native/macosx/screenlockdetector.dylib";
               try {
                   System.load(ScreenLockDetector.class.getResource(libPath).getPath());
                   System.out.println("Loaded screenlock detector library from: " + libPath);
               } catch (Exception e2) {
                   System.err.println("Failed to load native library: " + e2);
               }
           }
       }

       public native void startScreenLockDetection();
       public native void stopScreenLockDetection();
   }
   ```

4. **Verify Native Method Signatures**:
    - Use the `javap` command to verify that the native method signatures in the compiled `ScreenLockDetector` class match the JNI method names:
      ```bash
      javap -s com.hemendra.activity.systemevent.libs.ScreenLockDetector
      ```
      The output should show something like:
      ```bash
      public native void startScreenLockDetection();
          Signature: ()V
      public native void stopScreenLockDetection();
          Signature: ()V
      ```

5. **Use `nm` to Verify the Symbols**:
    - Run the `nm` command on the compiled `.dylib` to ensure that the symbols for the native methods (`startScreenLockDetection` and `stopScreenLockDetection`) are present:
      ```bash
      nm screenlockdetector.dylib | grep ScreenLockDetector
      ```
      The output should contain entries like:
      ```bash
      T _Java_com_hemendra_activity_systemevent_libs_ScreenLockDetector_startScreenLockDetection
      T _Java_com_hemendra_activity_systemevent_libs_ScreenLockDetector_stopScreenLockDetection
      ```

   If the symbols are missing, it means there was an issue during compilation or the method names do not match.


### Recap:

- Ensure the JNI method signatures in both the C++ code and the generated header file match exactly.
- Recompile the native library and verify the JNI symbols using `nm`.
- Double-check the architecture of both the JVM and the compiled `.dylib`.
- Use `javap` to verify the method signatures in the Java class.

## Author
[Hemendra Sethi](https://github.com/Hemendra1990)
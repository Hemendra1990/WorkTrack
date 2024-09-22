package com.hemendra.activity.systemevent.screenlock.mac;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MacOsScreenLockDetector {

    static {
        try {
            System.load("/System/Library/Frameworks/CoreFoundation.framework/Versions/A/CoreFoundation");
            log.info("CoreFoundation library loaded successfully");
            cf = Native.load("CoreFoundation", CoreFoundation.class);
        } catch (Exception e) {
            log.error("Failed to load CoreFoundation: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static final CoreFoundation cf;
    private static final int kCFStringEncodingUTF8 = 0x08000100;
    private SystemEventListener listener;
    private static MacOsScreenLockDetector instance;

    private MacOsScreenLockDetector() {
    }

    public static synchronized MacOsScreenLockDetector getInstance() {
        if (instance == null) {
            instance = new MacOsScreenLockDetector();
        }
        return instance;
    }

    public void setListener(SystemEventListener listener) {
        this.listener = listener;
    }

    private static String convertCFStringRefToString(CoreFoundation.CFStringRef cfString) {
        if (cfString == null) {
            return null;
        }

        long length = cf.CFStringGetLength(cfString);
        if (length == 0) {
            return "";
        }

        Pointer cStringPtr = cf.CFStringGetCStringPtr(cfString, kCFStringEncodingUTF8);
        if (cStringPtr != null) {
            return cStringPtr.getString(0, "UTF-8");
        }

        // Fallback method
        byte[] buffer = new byte[(int) (length * 4)]; // 4 bytes per character should be enough for UTF-8
        if (cf.CFStringGetCString(cfString, buffer, buffer.length, kCFStringEncodingUTF8)) {
            return Native.toString(buffer, "UTF-8");
        }

        return null;
    }

    private static final CFNotificationCallback callback = new CFNotificationCallback() {
        public void invoke(CoreFoundation.CFNotificationCenterRef center, Pointer observer,
                           CoreFoundation.CFStringRef name, Pointer object, Pointer userInfo) {
            String nameString = convertCFStringRefToString(name);
            if ("com.apple.screenIsLocked".equals(nameString)) {
                getInstance().listener.listenScreenLockEvent();
            } else if ("com.apple.screenIsUnlocked".equals(nameString)) {
                getInstance().listener.listenScreenUnLockEvent();
            }
        }
    };

    public void startDetection() {
        try {
            CoreFoundation.CFNotificationCenterRef center = cf.CFNotificationCenterGetDistributedCenter();
            if (center == null) {
                throw new RuntimeException("Failed to get distributed notification center");
            }
            CoreFoundation.CFStringRef lockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsLocked", kCFStringEncodingUTF8);
            CoreFoundation.CFStringRef unlockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsUnlocked", kCFStringEncodingUTF8);

            if (lockName == null || unlockName == null) {
                log.warn("Failed to create notification name strings {}", lockName);
            }
            cf.CFNotificationCenterAddObserver(center, Pointer.NULL, callback, lockName, null, 0);
            cf.CFNotificationCenterAddObserver(center, Pointer.NULL, callback, unlockName, null, 0);

            log.info("Screen lock detection started");
            // Release the created CFStrings to avoid memory leaks
            cf.CFRelease(lockName.getPointer());
            cf.CFRelease(unlockName.getPointer());

            // Run the main event loop
            CoreFoundation.CFRunLoopRef runLoop = cf.CFRunLoopGetCurrent();
            if (runLoop == null) {
                throw new RuntimeException("Failed to get current run loop");
            }

            // Use CFRunLoopRunInMode instead of CFRunLoopRun for more control
            CoreFoundation.CFStringRef modeName = cf.CFStringCreateWithCString(null, "kCFRunLoopDefaultMode", kCFStringEncodingUTF8);
            while (true) {
                cf.CFRunLoopRunInMode(modeName, 1.0, false);
            }
        } catch (Exception e) {
            log.error("Error in startDetection: " + e.getMessage());
        }
    }
}

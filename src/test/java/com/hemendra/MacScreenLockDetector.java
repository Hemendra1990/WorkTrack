package com.hemendra;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MacScreenLockDetector {

    public interface CoreFoundation extends Library {
        class CFStringRef extends PointerType {}
        class CFNotificationCenterRef extends PointerType {}
        class CFRunLoopRef extends PointerType {}

        CFNotificationCenterRef CFNotificationCenterGetDistributedCenter();
        void CFNotificationCenterAddObserver(CFNotificationCenterRef center, Pointer observer,
                                             CFNotificationCallback callBack, CFStringRef name,
                                             Pointer object, int suspensionBehavior);
        CFStringRef CFStringCreateWithCString(Pointer alloc, String cStr, int encoding);
        void CFRelease(Pointer obj);
        CFRunLoopRef CFRunLoopGetCurrent();
        void CFRunLoopRun();
        int CFRunLoopRunInMode(CFStringRef mode, double seconds, boolean returnAfterSourceHandled);
    }

    @FunctionalInterface
    public interface CFNotificationCallback extends com.sun.jna.Callback {
        void invoke(CoreFoundation.CFNotificationCenterRef center, Pointer observer,
                    CoreFoundation.CFStringRef name, Pointer object, Pointer userInfo);
    }

    private static final CoreFoundation cf;

    static {
        try {
            System.load("/System/Library/Frameworks/CoreFoundation.framework/Versions/A/CoreFoundation");
            System.out.println("CoreFoundation library loaded successfully");
            cf = Native.load("CoreFoundation", CoreFoundation.class);
        } catch (Exception e) {
            System.err.println("Failed to load CoreFoundation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static final CFNotificationCallback callback = new CFNotificationCallback() {
        public void invoke(CoreFoundation.CFNotificationCenterRef center, Pointer observer,
                           CoreFoundation.CFStringRef name, Pointer object, Pointer userInfo) {
            String nameString = name.toString(); // Convert CFStringRef to Java string
            System.out.println("Notification received: " + nameString);
            if (nameString.contains("com.apple.screenIsLocked")) {
                System.out.println("Screen locked");
            } else if (nameString.contains("com.apple.screenIsUnlocked")) {
                System.out.println("Screen unlocked");
            }
        }
    };

    public void startDetection() {
        try {
            CoreFoundation.CFNotificationCenterRef center = cf.CFNotificationCenterGetDistributedCenter();
            if (center == null) {
                throw new RuntimeException("Failed to get distributed notification center");
            }
            System.out.println("Got distributed notification center");

            CoreFoundation.CFStringRef lockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsLocked", 0);
            CoreFoundation.CFStringRef unlockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsUnlocked", 0);

            if (lockName == null || unlockName == null) {
                throw new RuntimeException("Failed to create notification name strings");
            }
            System.out.println("Created notification name strings");

            cf.CFNotificationCenterAddObserver(center, Pointer.NULL, callback, lockName, null, 0);
            cf.CFNotificationCenterAddObserver(center, Pointer.NULL, callback, unlockName, null, 0);
            System.out.println("Added observers");

            System.out.println("Screen lock detection started");

            // Release the created CFStrings to avoid memory leaks
            cf.CFRelease(lockName.getPointer());
            cf.CFRelease(unlockName.getPointer());

            // Run the main event loop
            CoreFoundation.CFRunLoopRef runLoop = cf.CFRunLoopGetCurrent();
            if (runLoop == null) {
                throw new RuntimeException("Failed to get current run loop");
            }
            System.out.println("Got current run loop, about to start running...");

            // Use CFRunLoopRunInMode instead of CFRunLoopRun for more control
            CoreFoundation.CFStringRef modeName = cf.CFStringCreateWithCString(null, "kCFRunLoopDefaultMode", 0);
            while (true) {
                int result = cf.CFRunLoopRunInMode(modeName, 1.0, false);
                //System.out.println("Run loop iteration completed with result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error in startDetection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MacScreenLockDetector detector = new MacScreenLockDetector();
        detector.startDetection();
    }
}
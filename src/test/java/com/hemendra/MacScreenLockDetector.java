package com.hemendra;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class MacScreenLockDetector {

    public interface CoreFoundation extends Library {
        class CFStringRef extends PointerType {
        }

        class CFNotificationCenterRef extends PointerType {
        }

        class CFRunLoopRef extends PointerType {
        }

        CFNotificationCenterRef CFNotificationCenterGetDistributedCenter();

        void CFNotificationCenterAddObserver(CFNotificationCenterRef center, Pointer observer,
                                             CFNotificationCallback callBack, CFStringRef name,
                                             Pointer object, int suspensionBehavior);

        CFStringRef CFStringCreateWithCString(Pointer alloc, String cStr, int encoding);

        void CFRelease(Pointer obj);

        CFRunLoopRef CFRunLoopGetCurrent();

        void CFRunLoopRun();

        int CFRunLoopRunInMode(CFStringRef mode, double seconds, boolean returnAfterSourceHandled);

        long CFStringGetLength(CFStringRef theString);

        Pointer CFStringGetCStringPtr(CFStringRef theString, int encoding);

        boolean CFStringGetCString(CFStringRef theString, byte[] buffer, long bufferSize, int encoding);
    }

    @FunctionalInterface
    public interface CFNotificationCallback extends com.sun.jna.Callback {
        void invoke(CoreFoundation.CFNotificationCenterRef center, Pointer observer,
                    CoreFoundation.CFStringRef name, Pointer object, Pointer userInfo);
    }

    private static final CoreFoundation cf;
    private static final int kCFStringEncodingUTF8 = 0x08000100;

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
            System.out.println("Notification received: " + nameString);
            if ("com.apple.screenIsLocked".equals(nameString)) {
                System.out.println("Screen locked");
            } else if ("com.apple.screenIsUnlocked".equals(nameString)) {
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

            CoreFoundation.CFStringRef lockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsLocked", kCFStringEncodingUTF8);
            CoreFoundation.CFStringRef unlockName = cf.CFStringCreateWithCString(null, "com.apple.screenIsUnlocked", kCFStringEncodingUTF8);

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
            CoreFoundation.CFStringRef modeName = cf.CFStringCreateWithCString(null, "kCFRunLoopDefaultMode", kCFStringEncodingUTF8);
            while (true) {
                int result = cf.CFRunLoopRunInMode(modeName, 1.0, false);
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
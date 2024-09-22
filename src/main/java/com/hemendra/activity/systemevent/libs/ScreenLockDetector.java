package com.hemendra.activity.systemevent.libs;

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
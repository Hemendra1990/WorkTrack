package com.hemendra.activity.systemevent.libs;

import com.hemendra.activity.systemevent.SystemEventListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemSleepDetector {
    private static SystemSleepDetector instance;
    private SystemEventListener listener;

    static {
        try {
            System.loadLibrary("systemSleepDetector");
        } catch (UnsatisfiedLinkError e) {
            String libPath = "/native/macosx/systemSleepDetector.dylib";
            try {
                System.load(SystemSleepDetector.class.getResource(libPath).getPath());
                log.info("Loaded screenlock detector library: " + libPath);
            } catch (Exception e2) {
                log.error("Failed to load native library: {}", e2);
            }
        }
    }


    private SystemSleepDetector() {}

    public static synchronized SystemSleepDetector getInstance() {
        if (instance == null) {
            instance = new SystemSleepDetector();
        }
        return instance;
    }


    public native void startScreenLockDetection();
    public native void stopScreenLockDetection();

    public void setListener(SystemEventListener listener) {
        this.listener = listener;
    }

    private void onScreenLocked() {
        // Handle screen locked event
        log.info("Screen locked");
        this.listener.listenScreenLockEvent();
    }

    private void onScreenUnlocked() {
        // Handle screen unlocked event
        log.info("Screen unlocked");
        this.listener.listenScreenUnLockEvent();
    }
}
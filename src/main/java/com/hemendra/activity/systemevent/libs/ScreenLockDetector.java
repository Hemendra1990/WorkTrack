package com.hemendra.activity.systemevent.libs;

import com.hemendra.activity.systemevent.SystemEventListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Slf4j
public class ScreenLockDetector {
    private static ScreenLockDetector instance;
    private SystemEventListener listener;

    static {
        try {
            System.loadLibrary("screenlockdetector");
        } catch (UnsatisfiedLinkError e) {
            String libPath = "/native/macosx/screenlockdetector.dylib";
            try {
                System.load(ScreenLockDetector.class.getResource(libPath).getPath());
                log.info("Loaded screenlock detector library: " + libPath);
            } catch (Exception e2) {
                log.error("Failed to load native library: {}", e2);
            }
        }
    }


    private ScreenLockDetector() {}

    public static synchronized ScreenLockDetector getInstance() {
        if (instance == null) {
            instance = new ScreenLockDetector();
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
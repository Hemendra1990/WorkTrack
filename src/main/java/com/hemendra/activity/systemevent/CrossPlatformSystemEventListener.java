package com.hemendra.activity.systemevent;

import com.hemendra.activity.systemevent.factory.SystemEventListenerFactory;
import com.hemendra.activity.systemevent.libs.SystemSleepDetector;
import com.hemendra.activity.systemevent.screenlock.mac.MacOsScreenLockDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrossPlatformSystemEventListener {
    private final SystemEventListenerFactory systemEventListenerFactory;

    public void runSystemEventListener() {
        SystemEventListener osSpecificSystemEventListener = systemEventListenerFactory.getOsSpecificSystemEventListener();
        Thread.ofVirtual().start(()-> {
            detectSystemSleep(osSpecificSystemEventListener);
        });

        Thread.ofVirtual().start(()-> {
            detectSystemScreenLock(osSpecificSystemEventListener);
        });
    }

    /**
     * Detect Screen lock in the macOs system
     *
     * @param osSpecificSystemEventListener
     */
    private static void detectSystemScreenLock(SystemEventListener osSpecificSystemEventListener) {
        MacOsScreenLockDetector macOsScreenLockDetector = MacOsScreenLockDetector.getInstance();
        macOsScreenLockDetector.setListener(osSpecificSystemEventListener);
        macOsScreenLockDetector.startDetection();
    }

    /**
     * Detects Screen lock in the macOs system
     *
     * @param osSpecificSystemEventListener
     */
    private static void detectSystemSleep(SystemEventListener osSpecificSystemEventListener) {
        SystemSleepDetector systemSleepDetector = SystemSleepDetector.getInstance();
        systemSleepDetector.setListener(osSpecificSystemEventListener);
        systemSleepDetector.startScreenLockDetection();
    }
}

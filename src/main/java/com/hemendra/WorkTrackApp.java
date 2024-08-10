package com.hemendra;

import com.hemendra.activity.UserActivityMonitor;
import com.hemendra.activity.apptracker.browser.CrossPlatformAppUsageTracker;
import com.hemendra.activity.apptracker.screenshot.CrossPlatformScreenshotTaker;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.config.WorkTrackConfig;
import com.hemendra.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hemendra
 * @since 10/08/2024
 */
@Slf4j
public class WorkTrackApp {
    public static void main(String[] args) {
        WorkTrackApp.run();
    }

    private static void run() {
        AnnotationConfigApplicationContext workTrackAppContext = new AnnotationConfigApplicationContext();
        workTrackAppContext.register(WorkTrackConfig.class);
        workTrackAppContext.refresh();

        WorkTrackProperties workTrackProperties = workTrackAppContext.getBean(WorkTrackProperties.class);
        log.info("Starting {} version {}", workTrackProperties.getAppName(), workTrackProperties.getAppVersion());

        Thread.ofVirtual().start(() -> {
            UserActivityMonitor activityMonitor = workTrackAppContext.getBean(UserActivityMonitor.class);
            activityMonitor.startMonitoring();
        });

        Thread.ofVirtual().start(() -> {
            CrossPlatformAppUsageTracker appUsageTracker = BeanUtils.getBean(CrossPlatformAppUsageTracker.class);
            appUsageTracker.runAppUsageTracker();
        });

        Thread.ofVirtual().start(() -> {
            CrossPlatformScreenshotTaker screenshotTaker = BeanUtils.getBean(CrossPlatformScreenshotTaker.class);
            try {
                screenshotTaker.runAppScreenshotTaker();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        while (true) {
            try {
                Thread.sleep(1000);  // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

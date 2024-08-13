package com.hemendra;

import com.hemendra.activity.UserActivityMonitor;
import com.hemendra.activity.apptracker.browser.CrossPlatformBrowserAppUsageTracker;
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
    public static void main(String[] args) throws InterruptedException {
        WorkTrackApp.run();
    }

    private static void run() throws InterruptedException {
        AnnotationConfigApplicationContext workTrackAppContext = new AnnotationConfigApplicationContext();
        workTrackAppContext.register(WorkTrackConfig.class);
        workTrackAppContext.refresh();

        WorkTrackProperties workTrackProperties = workTrackAppContext.getBean(WorkTrackProperties.class);
        log.info("Starting {} version {}", workTrackProperties.getAppName(), workTrackProperties.getAppVersion());

        Thread userActivityMonitor = Thread.ofVirtual().start(() -> {
            UserActivityMonitor activityMonitor = workTrackAppContext.getBean(UserActivityMonitor.class);
            activityMonitor.startMonitoring();
        });

        Thread.ofVirtual().start(() -> {
            CrossPlatformBrowserAppUsageTracker appUsageTracker = BeanUtils.getBean(CrossPlatformBrowserAppUsageTracker.class);
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

        userActivityMonitor.join();
    }
}

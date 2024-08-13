package com.hemendra.activity.apptracker.browser;

import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.AppUsageTrackerFactory;
import com.hemendra.activity.apptracker.BrowserTracker;
import com.hemendra.activity.apptracker.MacOsAppUsageTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CrossPlatformBrowserAppUsageTracker implements NativeMouseMotionListener {

    private final AppUsageTrackerFactory appUsageTrackerFactory;

    public void runAppUsageTracker() {
        AppUsageTracker appUsageTracker = appUsageTrackerFactory.getOsSpecificAppUsageTracker();

        while (true) {
            String activeWindow = appUsageTracker.getActiveWindowTitle();
            /*log.info("Active window: {}", activeWindow);*/
            //TODO: I am focusing more on MacOS, Later we will be focusing on Windows and Linux
            if (BrowserTracker.isBrowser(activeWindow)) {
                if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                    String browserUrl = macOsAppUsageTracker.getBrowserUrl(activeWindow);
                    macOsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                }
            } else {
                // If not a browser, reset the tracking
                if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                    macOsAppUsageTracker.resetTracking(null, null);
                }
            }
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
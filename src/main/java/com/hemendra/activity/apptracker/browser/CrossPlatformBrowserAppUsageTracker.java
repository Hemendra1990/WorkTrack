package com.hemendra.activity.apptracker.browser;

import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hemendra.activity.apptracker.*;
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

    private static String currentActiveWindow = "";

    public void runAppUsageTracker() {
        AppUsageTracker appUsageTracker = appUsageTrackerFactory.getOsSpecificAppUsageTracker();

        while (true) {
            String activeWindow = appUsageTracker.getActiveWindowTitle();

            if (!currentActiveWindow.equalsIgnoreCase(activeWindow)) {
                currentActiveWindow = activeWindow;

                if (BrowserTracker.isBrowser(activeWindow)) {
                    if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                        String browserUrl = macOsAppUsageTracker.getBrowserUrl(activeWindow);
                        macOsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                    } else if (appUsageTracker instanceof WindowsAppUsageTracker windowsAppUsageTracker) {
                        String browserUrl = windowsAppUsageTracker.getBrowserUrl(activeWindow);
                        windowsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                    }
                } else {
                    // If not a browser, reset the tracking
                    if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                        macOsAppUsageTracker.resetTracking(null, null);
                    }

                    if (appUsageTracker instanceof WindowsAppUsageTracker windowsAppUsageTracker) {
                        windowsAppUsageTracker.resetTracking(null, null);
                    }
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
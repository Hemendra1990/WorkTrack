package com.hemendra.activity.apptracker.browser;

import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.BrowserTracker;
import com.hemendra.activity.apptracker.trackerimpl.MacOsAppUsageTracker;
import com.hemendra.activity.apptracker.trackerimpl.WindowsAppUsageTracker;
import com.hemendra.activity.apptracker.trackerimpl.factory.AppUsageTrackerFactory;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.AppActivityDto;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.AppCategoryIdentifier;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CrossPlatformBrowserAppUsageTracker implements NativeMouseMotionListener {

    private final AppUsageTrackerFactory appUsageTrackerFactory;
    private final WorkTrackUtils workTrackUtils;
    private final WTHttpClient wtHttpClient;
    private final WorkTrackProperties workTrackProperties;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static String currentActiveWindow = "";
    LocalDateTime appStartTime = LocalDateTime.now();

    public void runAppUsageTracker() {
        AppUsageTracker appUsageTracker = appUsageTrackerFactory.getOsSpecificAppUsageTracker();

        while (true) {
            String activeWindow = appUsageTracker.getActiveWindowTitle();
            if (!currentActiveWindow.equalsIgnoreCase(activeWindow)) {
                LocalDateTime now = LocalDateTime.now();
                final LocalDateTime tAppStartTime = appStartTime;
                //TODO: THIS IS VERY CRITICAL CODE, MAY HAMPER THE PERFORMANCEs
                saveAppUsage(currentActiveWindow, tAppStartTime, now, Duration.between(tAppStartTime, now).getSeconds());
                appStartTime = now;
                currentActiveWindow = activeWindow;

                if (BrowserTracker.isBrowser(activeWindow)) {
                    String browserUrl = "";
                    if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                        browserUrl = macOsAppUsageTracker.getBrowserUrl(activeWindow);
                        macOsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                    } else if (appUsageTracker instanceof WindowsAppUsageTracker windowsAppUsageTracker) {
                        browserUrl = windowsAppUsageTracker.getBrowserUrl(activeWindow);
                        windowsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                    }
                    takeUrlScreenshot(browserUrl);
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
                log.error("Error while checking idle time: {}", e.getMessage());
            }
        }

    }

    private void takeUrlScreenshot(String browserUrl) {
        if (StringUtils.hasText(browserUrl)) {
            boolean isRestrictedWebsite = workTrackProperties.getRestrictedWebsites().stream().anyMatch(website -> browserUrl.toLowerCase().contains(website.toLowerCase()));
            if (isRestrictedWebsite) {
                applicationEventPublisher.publishEvent("takeScreenshot");
            }
        }
    }

    /**
     * Need to move to another class
     *
     * @param currentActiveWindow
     * @param appStartTime
     * @param now
     * @param durationInSeconds
     */
    private void saveAppUsage(String currentActiveWindow, LocalDateTime appStartTime, LocalDateTime now, long durationInSeconds) {
        String userName = workTrackUtils.getUserName();
        String macAddress = workTrackUtils.getMacAddress();

        AppActivityDto appActivityDto = new AppActivityDto();
        appActivityDto.setUserName(userName);
        appActivityDto.setMacAddress(macAddress);

        appActivityDto.setStartTime(appStartTime);
        appActivityDto.setEndTime(now);

        appActivityDto.setDuration(durationInSeconds);
        appActivityDto.setActiveWindow(currentActiveWindow);
        appActivityDto.setAppName(currentActiveWindow);
        appActivityDto.setAppCategory(AppCategoryIdentifier.getAppCategory(currentActiveWindow));

        wtHttpClient.logAppActivity(appActivityDto);
    }
}
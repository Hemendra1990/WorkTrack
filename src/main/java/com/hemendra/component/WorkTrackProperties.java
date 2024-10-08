package com.hemendra.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
public class WorkTrackProperties {
    private final String appName;
    private final String appVersion;
    private final int idleThresholdSeconds;
    private final boolean mouseMovementMonitorEnabled;
    private final List<String> monitoringBrowsers;
    private final int screenshotIntervalInMillis;
    private final String serverUserActivityUrl;
    private final String serverUserScreenshotUploadUrl;
    private final String serverUserWebsiteActivityUrl;
    private final List<String> restrictedWebsites;

    public WorkTrackProperties(@Value("${wt.app-name}") String appName,
                               @Value("${wt.app-version}") String appVersion,
                               @Value("${wt.idle-threshold.seconds}") int idleThresholdSeconds,
                               @Value("${wt.monitoring.mouse-movement.enabled}") boolean mouseMovementMonitorEnabled,
                               @Value("${wt.monitoring.browsers}") String monitoringBrowsers,
                               @Value("${wt.monitoring.screenshot.interval}") int screenshotIntervalInMillis,
                               @Value("${wt.monitoring.server.user-activity.url}") String serverUserActivityUrl,
                               @Value("${wt.monitoring.server.user-screenshot.url}") String serverUserScreenshotUploadUrl,
                               @Value("${wt.monitoring.server.user-website-activity.url}") String serverUserWebsiteActivityUrl,
                               @Value("${wt.monitoring.screenshot.restricted-websites}") String restrictedWebsites) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.idleThresholdSeconds = idleThresholdSeconds;
        this.mouseMovementMonitorEnabled = mouseMovementMonitorEnabled;
        this.screenshotIntervalInMillis = screenshotIntervalInMillis;
        this.serverUserActivityUrl = serverUserActivityUrl;
        this.serverUserScreenshotUploadUrl = serverUserScreenshotUploadUrl;
        this.serverUserWebsiteActivityUrl = serverUserWebsiteActivityUrl;


        if (monitoringBrowsers.contains(",")) {
            this.monitoringBrowsers = Arrays.stream(monitoringBrowsers.split(",")).map(String::trim).toList();
        } else {
            this.monitoringBrowsers = List.of(monitoringBrowsers);
        }

        if (restrictedWebsites.contains(",")) {
            this.restrictedWebsites = Arrays.stream(restrictedWebsites.split(",")).map(String::trim).toList();
        } else {
            this.restrictedWebsites = List.of(restrictedWebsites);
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public int getIdleThresholdSeconds() {
        return idleThresholdSeconds;
    }

    public boolean isMouseMovingMonitorEnabled() {
        return mouseMovementMonitorEnabled;
    }

    public List<String> getMonitoringBrowsers() {
        return new ArrayList<>(monitoringBrowsers);
    }

    public int getScreenshotIntervalInMillis() {
        return screenshotIntervalInMillis;
    }

    public String getServerUserActivityUrl() {
        return serverUserActivityUrl.trim();
    }

    public String getServerUserScreenshotUploadUrl() {
        return serverUserScreenshotUploadUrl.trim();
    }

    public String getServerUserWebsiteActivityUrl() {
        return serverUserWebsiteActivityUrl;
    }
    public List<String> getRestrictedWebsites() {
        return new ArrayList<>(restrictedWebsites);
    }
}

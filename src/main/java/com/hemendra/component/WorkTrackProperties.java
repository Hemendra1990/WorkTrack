package com.hemendra.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WorkTrackProperties {
    private final String appName;
    private final String appVersion;
    private final int idleThresholdSeconds;

    public WorkTrackProperties(
            @Value("${wt.app-name:Work Track}") String appName,
            @Value("${wt.app-version:1.0}") String appVersion,
            @Value("${wt.idle-threshold.seconds:30}") int idleThresholdSeconds) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.idleThresholdSeconds = idleThresholdSeconds;
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

}

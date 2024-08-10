package com.hemendra.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WorkTrackProperties {
    private final String appName;
    private final String appVersion;
    private final int idleThresholdSeconds;
    private final boolean mouseMovementMonitorEnabled;

    public WorkTrackProperties(@Value("${wt.app-name:Work Track}") String appName, @Value("${wt.app-version:1.0}") String appVersion, @Value("${wt.idle-threshold.seconds:30}") int idleThresholdSeconds, @Value("${wt.monitoring.mouse-movement.enabled:true}") boolean mouseMovementMonitorEnabled) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.idleThresholdSeconds = idleThresholdSeconds;
        this.mouseMovementMonitorEnabled = mouseMovementMonitorEnabled;
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

}

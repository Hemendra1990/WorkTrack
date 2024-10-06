package com.hemendra.activity.systemreset.impl;

import com.hemendra.activity.systemreset.SystemResetListener;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinuxSystemResetListener implements SystemResetListener {

    private static final int INACTIVITY_THRESHOLD_HOURS = 6;
    private final WorkTrackUtils workTrackUtils;

    private LocalDateTime lastActivityTime;
    private LocalDateTime lastShutdownTime;

    @Override
    public boolean isStartOfDay(LocalDateTime currentEventTime) {
        log.info("Checking if it's the start of the day on Linux...");

        if (lastShutdownTime != null) {
            Duration durationSinceShutdown = Duration.between(lastShutdownTime, currentEventTime);
            if (durationSinceShutdown.toHours() >= INACTIVITY_THRESHOLD_HOURS) {
                log.info("User has been inactive for more than {} hours. This is the start of the day.");
                return true;
            }
        }

        if (lastActivityTime != null) {
            Duration durationSinceLastActivity = Duration.between(lastActivityTime, currentEventTime);
            if (durationSinceLastActivity.toHours() >= INACTIVITY_THRESHOLD_HOURS) {
                log.info("User has been inactive for more than {} hours. This is the start of the day.");
                return true;
            }
        }

        return false;
    }

    @Override
    public void resetSystemState() {
        log.info("Resetting system state on Linux.");
        lastActivityTime = null;
        lastShutdownTime = null;
    }

    public void updateLastActivityTime(LocalDateTime activityTime) {
        this.lastActivityTime = activityTime;
    }

    public void updateLastShutdownTime(LocalDateTime shutdownTime) {
        this.lastShutdownTime = shutdownTime;
    }
}

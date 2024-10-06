package com.hemendra.activity.systemreset.impl;

import com.hemendra.activity.systemreset.SystemResetListener;
import com.hemendra.activity.systemreset.constant.Shift;
import com.hemendra.tray.stage.ShiftSelectionStageManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WindowsSystemResetListener implements SystemResetListener {

    private static final int INACTIVITY_THRESHOLD_HOURS = 6;
    private static final int RESTART_THRESHOLD_MINUTES = 60;

    private final ShiftSelectionStageManager shiftSelectionStageManager;

    private LocalDateTime lastActivityTime;
    private LocalDateTime lastShutdownTime;
    @Getter
    private Shift currentShift;
    @Getter
    private LocalTime shiftStartTime;
    @Getter
    private LocalTime shiftEndTime;

    @Override
    public boolean isStartOfDay(LocalDateTime currentEventTime) {
        log.info("Checking if it's the start of the day on Windows...");

        if (currentShift == null) {
            log.info("No shift set. Checking if it's a recent restart.");
            if (isRecentRestart(currentEventTime)) {
                log.info("Recent restart detected. Not considering as start of day.");
                return false;
            }
            log.info("No recent restart detected. Assuming start of day.");
            return true;
        }

        boolean isStartOfDay = false;

        if (lastShutdownTime != null) {
            Duration durationSinceShutdown = Duration.between(lastShutdownTime, currentEventTime);
            if (durationSinceShutdown.toHours() >= INACTIVITY_THRESHOLD_HOURS) {
                isStartOfDay = !wasLastActivityDuringShift(lastShutdownTime);
            }
        }

        if (lastActivityTime != null) {
            Duration durationSinceLastActivity = Duration.between(lastActivityTime, currentEventTime);
            if (durationSinceLastActivity.toHours() >= INACTIVITY_THRESHOLD_HOURS) {
                isStartOfDay = !wasLastActivityDuringShift(lastActivityTime);
            }
        }

        if (isStartOfDay) {
            log.info("User has been inactive for more than {} hours outside of shift time. This is the start of the day.", INACTIVITY_THRESHOLD_HOURS);
        } else {
            log.info("User's last activity was during shift time or within {} hours. Not considering as start of day.", INACTIVITY_THRESHOLD_HOURS);
        }

        return isStartOfDay;
    }

    private boolean isRecentRestart(LocalDateTime currentEventTime) {
        if (lastShutdownTime == null) {
            return false;
        }
        Duration durationSinceShutdown = Duration.between(lastShutdownTime, currentEventTime);
        return durationSinceShutdown.toMinutes() < RESTART_THRESHOLD_MINUTES;
    }

    private boolean wasLastActivityDuringShift(LocalDateTime activityTime) {
        if (currentShift == null || shiftStartTime == null || shiftEndTime == null) {
            log.warn("Shift information is not set. Unable to determine if activity was during shift.");
            return false;
        }

        LocalTime activityTimeOfDay = activityTime.toLocalTime();

        // Handle overnight shifts
        if (shiftStartTime.isAfter(shiftEndTime)) {
            // For overnight shifts, we need to consider two scenarios:
            // 1. Activity time is after shift start time on the same day
            // 2. Activity time is before shift end time on the next day
            if (!activityTimeOfDay.isBefore(shiftStartTime)) {
                return true;
            }
            if (!activityTimeOfDay.isAfter(shiftEndTime)) {
                return true;
            }
        } else {
            // For regular shifts, simply check if the activity time is between start and end times
            if (!activityTimeOfDay.isBefore(shiftStartTime) && !activityTimeOfDay.isAfter(shiftEndTime)) {
                return true;
            }
        }

        return false;
    }

    public void promptShiftSelection() {
        shiftSelectionStageManager.launchShiftSelectionScene(this::updateShift);
    }

    public void updateShift(Shift shift) {
        this.currentShift = shift;
        this.shiftStartTime = shift.getStartTime();
        this.shiftEndTime = shift.getEndTime();
        log.info("Updated shift: {}. Start time: {}, End time: {}", shift, shiftStartTime, shiftEndTime);
    }

    @Override
    public void resetSystemState() {
        log.info("Resetting system state on Windows.");
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

package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.hemendra.activity.systemevent.service.SleepActivityLogService;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.tray.stage.AwayFromSystemStageManager;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MacOsSystemEventListener implements SystemEventListener {

    private final AwayFromSystemStageManager awayFromSystemStage;
    private final SleepActivityLogService sleepActivityLogService;

    private static UUID uniqueScreenLockSessionId = UUID.randomUUID();
    private static LocalDateTime screenLockStartTime = LocalDateTime.now();

    private static UUID uniqueSystemSleepSessionId = UUID.randomUUID();
    private static LocalDateTime systemSleepStartTime = LocalDateTime.now();

    @Override
    public void listenScreenLockEvent() {
        log.info("Screen lock event received at {}", LocalDateTime.now().toString());
        uniqueScreenLockSessionId = UUID.randomUUID();
        screenLockStartTime = LocalDateTime.now();
        sleepActivityLogService.saveActivityLog(ActivityType.LOCK, screenLockStartTime, null, 0, uniqueScreenLockSessionId, ActivityState.START);
    }

    @Override
    public void listenScreenUnLockEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Screen unlock event received at {}", now.toString());
        sleepActivityLogService.saveActivityLog(ActivityType.LOCK, screenLockStartTime, now, Duration.between(screenLockStartTime, now).getSeconds(), uniqueScreenLockSessionId, ActivityState.END);
        Platform.runLater(() -> {
            awayFromSystemStage.launchSystemAwayScene(ActivityType.LOCK, screenLockStartTime, now, Duration.between(screenLockStartTime, now).getSeconds(), uniqueScreenLockSessionId);
            uniqueScreenLockSessionId = UUID.randomUUID();
        });
    }

    @Override
    public void listenSystemEvent() {

    }

    @Override
    public void listenShutdownEvent() {

    }

    @Override
    public void systemSleepStartEvent() {
        log.info("System went into sleep at {}", LocalDateTime.now().toString());
        uniqueSystemSleepSessionId = UUID.randomUUID();
        systemSleepStartTime = LocalDateTime.now();
        sleepActivityLogService.saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, null, 0, uniqueSystemSleepSessionId, ActivityState.START);
    }

    @Override
    public void systemSleepEndEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("System wakeup from sleep at {}", now.toString());
        sleepActivityLogService.saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, now, Duration.between(systemSleepStartTime, now).getSeconds(), uniqueSystemSleepSessionId, ActivityState.END);
        Platform.runLater(() -> {
            awayFromSystemStage.launchSystemAwayScene(ActivityType.SLEEP, systemSleepStartTime, now, Duration.between(systemSleepStartTime, now).getSeconds(), uniqueSystemSleepSessionId);
        });
    }
}

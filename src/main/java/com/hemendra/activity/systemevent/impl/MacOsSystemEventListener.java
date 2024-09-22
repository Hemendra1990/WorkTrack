package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.hemendra.dto.UserActivityDto;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
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

    private final WorkTrackUtils workTrackUtils;
    private final WTHttpClient httpClient;

    private static UUID uniqueScreenLockSessionId = UUID.randomUUID();
    private static LocalDateTime screenLockStartTime = LocalDateTime.now();

    private static UUID uniqueSystemSleepSessionId = UUID.randomUUID();
    private static LocalDateTime systemSleepStartTime = LocalDateTime.now();

    @Override
    public void listenScreenLockEvent() {
        log.info("Screen lock event received at {}", LocalDateTime.now().toString());
        uniqueScreenLockSessionId = UUID.randomUUID();
        screenLockStartTime = LocalDateTime.now();
        saveActivityLog(ActivityType.LOCK, screenLockStartTime, null, 0, uniqueScreenLockSessionId, ActivityState.START);
    }

    @Override
    public void listenScreenUnLockEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Screen unlock event received at {}", now.toString());
        saveActivityLog(ActivityType.LOCK, screenLockStartTime, now, Duration.between(screenLockStartTime, now).getSeconds(), uniqueScreenLockSessionId, ActivityState.END);
        uniqueScreenLockSessionId = UUID.randomUUID();
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
        saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, null, 0, uniqueSystemSleepSessionId, ActivityState.START);
    }

    @Override
    public void systemSleepEndEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("System wakeup from sleep at {}", now.toString());
        saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, now, Duration.between(systemSleepStartTime, now).getSeconds(), uniqueSystemSleepSessionId, ActivityState.END);
    }

    public void saveActivityLog(ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long duration, UUID sessionId, ActivityState state) {
        String userName = workTrackUtils.getUserName();
        String macAddress = workTrackUtils.getMacAddress();

        log.debug("Saving activity log to the database. User: {}, MAC address: {}, Activity type: {}, Start time: {}, End time: {}, Duration: {} seconds.",
                userName, macAddress, activityType, startTime, endTime, duration);
        UserActivityDto userActivityDto = new UserActivityDto();
        userActivityDto.setUserName(userName);
        userActivityDto.setMacAddress(macAddress);
        userActivityDto.setActivityType(activityType);
        userActivityDto.setStartTime(startTime);
        userActivityDto.setEndTime(endTime);
        userActivityDto.setDuration(duration);
        userActivityDto.setSessionId(sessionId);
        userActivityDto.setState(state);

        httpClient.logUserActivity(userActivityDto);
    }
}

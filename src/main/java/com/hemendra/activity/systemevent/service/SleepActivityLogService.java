package com.hemendra.activity.systemevent.service;

import com.hemendra.dto.UserActivityDto;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SleepActivityLogService {

    private final WorkTrackUtils workTrackUtils;
    private final WTHttpClient httpClient;

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

    public void saveActivityLogWithReason(String reason, String activity, ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long duration, UUID sessionId, ActivityState state) {
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

        userActivityDto.setReason(reason);
        userActivityDto.setActivity(activity);

        httpClient.logUserActivity(userActivityDto);
    }
}

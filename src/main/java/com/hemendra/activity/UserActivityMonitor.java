package com.hemendra.activity;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.UserActivityDto;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Slf4j
@Component
public class UserActivityMonitor implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener, InitializingBean {

    @Autowired
    private WorkTrackProperties workTrackProperties;
    @Autowired
    private WorkTrackUtils workTrackUtils;
    @Autowired
    private WTHttpClient wtHttpClient;

    private LocalDateTime lastActivityTime;
    private LocalDateTime idleStartTime;
    private LocalDateTime userInitialStartTime; //when the program starts
    private boolean isIdle = false;
    private static UUID sessionId;

    public UserActivityMonitor() {
        this.lastActivityTime = LocalDateTime.now();
        this.userInitialStartTime = LocalDateTime.now();
        sessionId = UUID.randomUUID();
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
    }


    public void startMonitoring() {
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
            // Monitor idle and active state in an infinite loop
            while (true) {
                this.checkIdleTime();
                try {
                    Thread.sleep(1000);  // Check every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkIdleTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        long idleDuration = java.time.Duration.between(this.lastActivityTime, currentTime).getSeconds();

        if (!isIdle && idleDuration >= workTrackProperties.getIdleThresholdSeconds()) {
            log.debug("User was active for: " + idleDuration + " seconds.");
            saveActivityLog(ActivityType.ACTIVE, null, currentTime, 0, sessionId, ActivityState.END); //for the same session id the users was active for.....
            // Transition from active to idle
            isIdle = true;
            idleStartTime = currentTime;
            log.debug("User went idle at: " + idleStartTime);
            //Enter into idle state
            sessionId = UUID.randomUUID();
            saveActivityLog(ActivityType.IDLE, idleStartTime, null, 0, sessionId, ActivityState.START);
        } else if (isIdle && idleDuration < workTrackProperties.getIdleThresholdSeconds()) {
            // Transition from idle to active
            isIdle = false;
            log.debug("User resumed activity at: " + currentTime);
            log.debug("User was idle for: " + idleDuration + " seconds.");
            lastActivityTime = currentTime;  // Reset last activity time
            saveActivityLog(ActivityType.ACTIVE, idleStartTime, currentTime, idleDuration, sessionId, ActivityState.START);
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        updateLastActivityTime();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        updateLastActivityTime();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        updateLastActivityTime();
    }

    /**
     * User can hack this by moving randomly by simulating mouse movement
     * So, I will set it in properties file to enable or disable mouse movement monitoring
     *
     * @param nativeEvent
     */
    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        if (workTrackProperties.isMouseMovingMonitorEnabled()) {
            updateLastActivityTime();
        }
    }

    private void updateLastActivityTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        if (isIdle) {
            log.debug("User resumed activity at: {}", currentTime);
            long idleDuration = java.time.Duration.between(idleStartTime, currentTime).getSeconds();
            saveActivityLog(ActivityType.IDLE, null, currentTime, idleDuration, sessionId, ActivityState.END);
            log.debug("User was idle for: {} seconds.", idleDuration);
            isIdle = false;
            sessionId = UUID.randomUUID();
            saveActivityLog(ActivityType.ACTIVE, currentTime, null, 0, sessionId, ActivityState.START);
        }
        lastActivityTime = currentTime;
    }

    private void saveActivityLog(ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long duration, UUID sessionId, ActivityState state) {
        String userName = workTrackUtils.getUserName();
        String macAddress = workTrackUtils.getMacAddress();

        log.debug("Saving activity log to the database. User: {}, MAC address: {}, Activity type: {}, Start time: {}, End time: {}, Duration: {} seconds.",
                userName, macAddress, activityType, startTime, endTime, duration);
        UserActivityDto userActivityDto = new UserActivityDto();
        userActivityDto.setUserName(userName);
        userActivityDto.setUserName("subrat");//TODO: remove this before commit
        userActivityDto.setMacAddress(macAddress);
        userActivityDto.setActivityType(activityType);
        userActivityDto.setStartTime(startTime);
        userActivityDto.setEndTime(endTime);
        userActivityDto.setDuration(duration);
        userActivityDto.setSessionId(sessionId);
        userActivityDto.setState(state);

        wtHttpClient.logUserActivity(userActivityDto);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        saveActivityLog(ActivityType.ACTIVE, userInitialStartTime, null, 0, sessionId, ActivityState.START);
    }
}

package com.hemendra.activity;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.UserActivityDto;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Slf4j
@Component
public class UserActivityMonitor implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

    @Autowired
    private WorkTrackProperties workTrackProperties;
    @Autowired
    private WorkTrackUtils workTrackUtils;
    @Autowired
    private WTHttpClient wtHttpClient;

    public UserActivityMonitor() {
        this.lastActivityTime = LocalDateTime.now();
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
    }

    private LocalDateTime lastActivityTime;
    private LocalDateTime idleStartTime;
    private boolean isIdle = false;


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
            // Transition from active to idle
            isIdle = true;
            idleStartTime = currentTime;
            log.debug("User went idle at: " + idleStartTime);
            saveActivityLog(ActivityType.IDLE, lastActivityTime, idleStartTime, idleDuration);
        } else if (isIdle && idleDuration < workTrackProperties.getIdleThresholdSeconds()) {
            // Transition from idle to active
            isIdle = false;
            log.debug("User resumed activity at: " + currentTime);
            log.debug("User was idle for: " + idleDuration + " seconds.");
            lastActivityTime = currentTime;  // Reset last activity time
            saveActivityLog(ActivityType.ACTIVE, idleStartTime, currentTime, idleDuration);
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
            log.debug("User was idle for: {} seconds.", idleDuration);
            isIdle = false;
            saveActivityLog(ActivityType.ACTIVE, idleStartTime, currentTime, idleDuration);
        }
        lastActivityTime = currentTime;
    }

    private void saveActivityLog(ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long duration) {
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

        wtHttpClient.logUserActivity(userActivityDto);

        /*String insertSQL = "INSERT INTO user_activity_log (user_id, session_id, activity_type, activity_start_time, activity_end_time, duration_seconds, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, "user123"); // Replace with actual user ID
            pstmt.setString(2, "session456"); // Replace with actual session ID
            pstmt.setString(3, activityType);
            pstmt.setTimestamp(4, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(5, Timestamp.valueOf(endTime));
            pstmt.setLong(6, duration);
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();
            log.debug("Activity log saved to the database.");

        } catch (SQLException e) {
            log.error("Failed to save activity log to the database.", e);
        }*/
    }
}

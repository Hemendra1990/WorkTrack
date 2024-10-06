package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.hemendra.activity.systemevent.screenlock.win.MyUser32;
import com.hemendra.activity.systemevent.screenlock.win.WindowsScreenLockDetector;
import com.hemendra.activity.systemevent.screenlock.win.WindowsSystemMonitor;
import com.hemendra.activity.systemevent.service.SleepActivityLogService;
import com.hemendra.activity.systemreset.impl.WindowsSystemResetListener;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.tray.stage.AwayFromSystemStageManager;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Wtsapi32;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WindowsSystemEventListener implements SystemEventListener {

    private final AwayFromSystemStageManager awayFromSystemStage;
    private final SleepActivityLogService sleepActivityLogService;
    private final WindowsSystemResetListener systemResetListener;

    private static UUID uniqueScreenLockSessionId = UUID.randomUUID();
    private static LocalDateTime screenLockStartTime = LocalDateTime.now();

    private static UUID uniqueSystemSleepSessionId = UUID.randomUUID();
    private static LocalDateTime systemSleepStartTime = LocalDateTime.now();

    private static final Path LOG_FILE = Paths.get("build/logs/activity_log.json");

    static {
        try {
            Path logDirectory = FileSystems.getDefault().getPath("build/logs");
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }

            if (!Files.exists(LOG_FILE)) {
                Files.createFile(LOG_FILE);
                Files.write(LOG_FILE, new JSONArray().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); // Initialize with empty JSON array
                log.info("Log file created at: {}", LOG_FILE.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Error creating log directory or file: {}", e.getMessage());
        }
    }

    @Override
    public void listenScreenLockEvent() {
        log.info("Screen lock event received at {}", LocalDateTime.now().toString());
        uniqueScreenLockSessionId = UUID.randomUUID();
        screenLockStartTime = LocalDateTime.now();
        sleepActivityLogService.saveActivityLog(ActivityType.LOCK, screenLockStartTime, null, 0, uniqueScreenLockSessionId, ActivityState.START);

        //update reset last activity time
        systemResetListener.updateLastActivityTime(screenLockStartTime);
    }

    @Override
    public void listenScreenUnLockEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Screen unlock event received at {}", now.toString());
        sleepActivityLogService.saveActivityLog(ActivityType.LOCK, screenLockStartTime, now, Duration.between(screenLockStartTime, now).getSeconds(), uniqueScreenLockSessionId, ActivityState.END);

        //when system is getting unlocked check whether user is starting his/her day
        boolean startOfDay = systemResetListener.isStartOfDay(now);
        if(startOfDay) {
            Platform.runLater(() -> {
                systemResetListener.promptShiftSelection();
                systemResetListener.resetSystemState();
                uniqueScreenLockSessionId = UUID.randomUUID();
            });
        } else {
            Platform.runLater(() -> {
                awayFromSystemStage.launchSystemAwayScene(ActivityType.LOCK, screenLockStartTime, now, Duration.between(screenLockStartTime, now).getSeconds(), uniqueScreenLockSessionId);
                uniqueScreenLockSessionId = UUID.randomUUID();
            });
        }

    }

    @Override
    public void listenSystemEvent() {
        log.info("Listening to system events on Windows...");

        if (!com.sun.jna.Platform.isWindows()) {
            throw new UnsupportedOperationException("This implementation only supports Windows.");
        }

        checkShutdownEvent();

        WinUser.WNDCLASSEX wndClass = new WinUser.WNDCLASSEX();
        final String windowClassName = "WindowsSystemEventListener";

        wndClass.lpfnWndProc = new WindowsSystemMonitor();
        wndClass.hInstance = Kernel32.INSTANCE.GetModuleHandle("");
        wndClass.lpszClassName = windowClassName;

        if (MyUser32.INSTANCE.RegisterClassEx(wndClass).intValue() == 0) {
            log.error("Failed to register window class.");
            return;
        }

        WinDef.HWND hWnd = MyUser32.INSTANCE.CreateWindowEx(0, windowClassName, "System Event Listener",
                0, 0, 0, 0, 0, null, null, wndClass.hInstance, null);

        if (hWnd == null) {
            log.error("Failed to create window.");
            return;
        }

        // Register for session change notifications (lock/unlock)
        Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION);

        // Message loop to listen for system events
        WinUser.MSG msg = new WinUser.MSG();
        while (MyUser32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
            MyUser32.INSTANCE.TranslateMessage(msg);
            MyUser32.INSTANCE.DispatchMessage(msg);
        }

        WindowsScreenLockDetector.getInstance().setHWnd(hWnd);
    }

    @Override
    public void listenShutdownEvent() {
        log.info("Shutdown event detected on Windows.");
        logEvent("SHUTDOWN");
        WindowsScreenLockDetector.getInstance().stopDetection();
    }

    @Override
    public void systemSleepStartEvent() {
        log.info("Screen lock event received at {}", LocalDateTime.now().toString());
        uniqueSystemSleepSessionId = UUID.randomUUID();
        systemSleepStartTime = LocalDateTime.now();
        sleepActivityLogService.saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, null, 0, uniqueSystemSleepSessionId, ActivityState.START);

        systemResetListener.updateLastActivityTime(systemSleepStartTime);
    }

    @Override
    public void systemSleepEndEvent() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Screen unlock event received at {}", now.toString());
        sleepActivityLogService.saveActivityLog(ActivityType.SLEEP, systemSleepStartTime, now, Duration.between(systemSleepStartTime, now).getSeconds(), uniqueSystemSleepSessionId, ActivityState.END);

        boolean startOfDay = systemResetListener.isStartOfDay(now);
        if(startOfDay) {
            Platform.runLater(() -> {
                systemResetListener.promptShiftSelection();
                systemResetListener.resetSystemState();
                uniqueSystemSleepSessionId = UUID.randomUUID();
            });
        } else {
            Platform.runLater(() -> {
                awayFromSystemStage.launchSystemAwayScene(ActivityType.SLEEP, systemSleepStartTime, now, Duration.between(systemSleepStartTime, now).getSeconds(), uniqueSystemSleepSessionId);
                uniqueSystemSleepSessionId = UUID.randomUUID();
            });
        }
    }

    private void checkShutdownEvent() {
        try {
            // Step 1: Read logs from the file
            JSONArray logs = readLogs();

            if (!logs.isEmpty()) {
                // Get the last event from the logs
                JSONObject lastEvent = logs.getJSONObject(logs.length() - 1);
                String activityType = lastEvent.getString("activity_type");

                log.info("Found {} events in the log file.", logs.length());
                log.info("Last event type: {}", activityType);

                // Check if the last event was a system shutdown
                if ("SHUTDOWN".equalsIgnoreCase(activityType)) {
                    log.info("Last event was a system shutdown. Processing logs.");
                    isSystemBootingAfterThresholdTime(lastEvent);
                    processAndClearLog();  // Process the logs and clear the log file
                } else {
                    log.info("Last event was not a shutdown. No action taken.");
                }
            } else {
                log.info("Log file is empty. No events to process.");
            }

        } catch (IOException e) {
            log.error("Error occurred while reading the log file: {}", e.getMessage());
        }
    }

    private void isSystemBootingAfterThresholdTime(JSONObject lastEvent) {
        String activityType = lastEvent.getString("activity_type");
        if ("SHUTDOWN".equalsIgnoreCase(activityType)) {
            LocalDateTime startTime = LocalDateTime.parse(lastEvent.getString("start_time"));
            LocalDateTime endTime = LocalDateTime.now();
            systemResetListener.updateLastShutdownTime(startTime);
            boolean startOfDay = systemResetListener.isStartOfDay(endTime);
            if(startOfDay) {
                Platform.runLater(() -> {
                    systemResetListener.promptShiftSelection();
                    systemResetListener.resetSystemState();
                });
            } else {
                //When user is restarting the system within RESTART_THRESHOLD_MINUTES then consider that user was away from system
                Platform.runLater(() -> {
                    awayFromSystemStage.launchSystemAwayScene(ActivityType.RESTART, startTime, endTime, Duration.between(startTime, endTime).getSeconds(), UUID.randomUUID());
                });
            }
        }
    }

    private JSONArray readLogs() throws IOException {
        if (!Files.exists(LOG_FILE) || Files.size(LOG_FILE) == 0) {
            return new JSONArray();
        }
        String content = new String(Files.readAllBytes(LOG_FILE));
        try {
            return new JSONArray(content);
        } catch (org.json.JSONException e) {
            log.error("Invalid JSON format in file: {}", e.getMessage());
            return new JSONArray();
        }
    }

    private void processAndClearLog() {
        try {
            JSONArray events = readLogs();
            if (!events.isEmpty()) {
                insertEventsIntoUserActivity(events);  // Insert events into the database
                clearLogFile();
            }
        } catch (IOException e) {
            log.error("Error processing and clearing logs: {}", e.getMessage());
        }
    }

    private void clearLogFile() throws IOException {
        Files.write(LOG_FILE, new JSONArray().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void insertEventsIntoUserActivity(JSONArray events) {
        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject event = events.getJSONObject(i);

                LocalDateTime startTime = LocalDateTime.parse(event.getString("start_time"));
                LocalDateTime endTime = LocalDateTime.now();
                UUID sessionId = UUID.fromString(event.getString("session_id"));

                sleepActivityLogService.saveActivityLog(ActivityType.SHUTDOWN, startTime, endTime, Duration.between(screenLockStartTime, endTime).getSeconds(), sessionId, ActivityState.END);
            } catch (Exception e) {
                log.error("Error processing event: {}", e.getMessage());
            }
        }
    }

    private void logEvent(String activityType) {
        try {
            JSONArray logs = readLogs();
            JSONObject event = new JSONObject();
            LocalDateTime startTime = LocalDateTime.now();
            UUID sessionId = UUID.randomUUID(); // Generate new session id

            // Prepare the event object for logging
            event.put("activity_type", activityType);
            event.put("start_time", startTime.toString());
            event.put("session_id", sessionId);
            event.put("state", activityType);

            logs.put(event);

            // Write the updated logs back to the file
            Files.write(LOG_FILE, logs.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Error writing event to log file: {}", e.getMessage());
        }
    }
}

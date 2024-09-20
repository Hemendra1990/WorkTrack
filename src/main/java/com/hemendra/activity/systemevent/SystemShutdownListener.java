package com.hemendra.activity.systemevent;

import com.hemendra.activity.UserActivityMonitor;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.util.BeanUtils;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.win32.W32APIOptions;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SystemShutdownListener {

    private static LocalDateTime sleepStartTime = null;
    private static LocalDateTime lockStartTime = null;
    private static final Path LOG_FILE = Paths.get("src/main/resources/activity_log.json");

    // Define User32 Interface for JNA
    public interface MyUser32 extends User32 {
        MyUser32 INSTANCE = Native.load("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);

        WinDef.LRESULT DefWindowProc(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam);
    }

    // Constants for power and session change events
    public static final int WM_QUERYENDSESSION = 0x0011;
    public static final int WM_ENDSESSION = 0x0016;
    public static final int WM_POWERBROADCAST = 0x0218;
    public static final int PBT_APMSUSPEND = 0x0004; // System is suspending (sleep)
    public static final int PBT_APMRESUMESUSPEND = 0x0007; // System is resuming from sleep
    public static final int WM_WTSSESSION_CHANGE = 0x02B1;  // Session change
    public static final int WTS_SESSION_LOCK = 0x7;         // Session is locked
    public static final int WTS_SESSION_UNLOCK = 0x8;       // Session is unlocked

    // Window Procedure to handle Windows messages
    public static class WindowProc implements WinUser.WindowProc {
        @Override
        public WinDef.LRESULT callback(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam) {
            switch (uMsg) {
                case WM_QUERYENDSESSION:
                    logEvent("SHUTDOWN", null);
                    return new WinDef.LRESULT(1);

                case WM_POWERBROADCAST:
                    handlePowerEvent(uParam.intValue());
                    break;

                case WM_WTSSESSION_CHANGE:
                    handleSessionChange(uParam.intValue());
                    break;

                default:
                    return MyUser32.INSTANCE.DefWindowProc(hWnd, uMsg, uParam, lParam);
            }
            return new WinDef.LRESULT(1);
        }
    }

    // Method to log or update events into a JSON file
    private static void logEvent(String activityType, LocalDateTime endTime) {
        try {
            JSONArray logs = readLogs();
            JSONObject event = new JSONObject();

            LocalDateTime startTime = LocalDateTime.now();
            UUID sessionId = UUID.randomUUID(); // Generate new session id

            // Prepare the event object for logging
            event.put("activity_type", activityType);
            event.put("start_time", startTime.toString());
            event.put("end_time", endTime != null ? endTime.toString() : JSONObject.NULL);
            event.put("duration", JSONObject.NULL);
            event.put("session_id", sessionId);
            event.put("state", activityType);

            // Add the event to the logs
            logs.put(event);

            // Write the updated logs back to the file
            Files.write(LOG_FILE, logs.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Error writing event to log file: {}", e.getMessage());
        }
    }

    // Method to read logs from the JSON file
    private static JSONArray readLogs() throws IOException {
        if (!Files.exists(LOG_FILE) || Files.size(LOG_FILE) == 0) {
            // If the file doesn't exist or is empty, return an empty JSONArray
            return null;
        }

        String content = new String(Files.readAllBytes(LOG_FILE));
        try {
            return new JSONArray(content);
        } catch (org.json.JSONException e) {
            log.error("Invalid JSON format in file, returning empty log: {}", e.getMessage());
            return null;
        }
    }


    // Method to handle power management events like sleep and resume
    private static void handlePowerEvent(int powerEvent) {
        switch (powerEvent) {
            case PBT_APMSUSPEND:
                sleepStartTime = LocalDateTime.now();
                logEvent("SLEEP", null);  // Log the sleep event
                break;

            case PBT_APMRESUMESUSPEND:
                if (sleepStartTime != null) {
                    LocalDateTime wakeTime = LocalDateTime.now();
                    updateSleepEvent(wakeTime);  // Update the sleep event with the resume time

                    processAndClearLog();

                    sleepStartTime = null;  // Reset the sleep start time
                }
                break;
        }
    }

    // Method to handle session change events like lock and unlock
    private static void handleSessionChange(int sessionEvent) {
        switch (sessionEvent) {
            case WTS_SESSION_LOCK:
                lockStartTime = LocalDateTime.now();
                logEvent("LOCK", null);  // Log the lock event
                break;

            case WTS_SESSION_UNLOCK:
                if (lockStartTime != null) {
                    LocalDateTime unlockTime = LocalDateTime.now();
                    updateLockEvent(unlockTime);  // Update the lock event with the unlock time

                    processAndClearLog();

                    lockStartTime = null;  // Reset the lock time
                }
                break;
        }
    }

    private static void processAndClearLog() {
        try {
            JSONArray events = readLogs();
            if (events != null && !events.isEmpty()) {
                insertEventsIntoUserActivity(events);  // Insert events into the database
                clearLogFile();
            }
        } catch (IOException e) {
            log.error("Error occurred while processing or clearing log: {}", e.getMessage());
        }
    }

    // Method to update the most recent LOCK event with unlock time and duration
    private static void updateLockEvent(LocalDateTime unlockTime) {
        try {
            JSONArray logs = readLogs();

            if(logs != null) {
                // Find the last LOCK event with no end_time
                for (int i = logs.length() - 1; i >= 0; i--) {
                    JSONObject event = logs.getJSONObject(i);
                    if ("LOCK".equals(event.getString("activity_type")) && event.isNull("end_time")) {
                        // Calculate the duration
                        LocalDateTime lockStartTime = LocalDateTime.parse(event.getString("start_time"));
                        Duration lockDuration = Duration.between(lockStartTime, unlockTime);

                        // Update the LOCK event
                        event.put("end_time", unlockTime.toString());
                        event.put("duration", lockDuration.getSeconds());  // Store the duration in seconds
                        event.put("state", "LOCK_COMPLETE");

                        // Update the log file with the modified event
                        Files.write(LOG_FILE, logs.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while updating LOCK event: {}", e.getMessage());
        }
    }

    // Method to update the most recent SLEEP event with resume time and duration
    private static void updateSleepEvent(LocalDateTime wakeTime) {
        try {
            JSONArray logs = readLogs();

            if(logs != null) {
                // Find the last SLEEP event with no end_time
                for (int i = logs.length() - 1; i >= 0; i--) {
                    JSONObject event = logs.getJSONObject(i);
                    if ("SLEEP".equals(event.getString("activity_type")) && event.isNull("end_time")) {
                        // Calculate the duration
                        LocalDateTime sleepStartTime = LocalDateTime.parse(event.getString("start_time"));
                        Duration sleepDuration = Duration.between(sleepStartTime, wakeTime);

                        // Update the SLEEP event
                        event.put("end_time", wakeTime.toString());
                        event.put("duration", sleepDuration.getSeconds());
                        event.put("state", "SLEEP_COMPLETE");

                        // Update the log file with the modified event
                        Files.write(LOG_FILE, logs.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while updating SLEEP event: {}", e.getMessage());
        }
    }

    // Insert events into the database (dummy method for demonstration)
    public static void insertEventsIntoUserActivity(JSONArray events) {
        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject event = events.getJSONObject(i);

                // Extract fields from the JSON event
                String activityTypeStr = event.getString("activity_type");
                ActivityType activityType = ActivityType.valueOf(activityTypeStr.toUpperCase());

                String startTimeStr = event.getString("start_time");
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr);

                LocalDateTime endTime;
                long duration;

                UUID sessionId = UUID.fromString(event.getString("session_id"));

                String stateStr = event.getString("state");
                ActivityState state = ActivityState.valueOf(stateStr.toUpperCase());

                if ("SHUTDOWN".equalsIgnoreCase(activityTypeStr)) {
                    endTime = LocalDateTime.now();  // End time is now
                    duration = java.time.Duration.between(startTime, endTime).getSeconds();  // Calculate duration
                } else {
                    String endTimeStr = event.optString("end_time", null);
                    endTime = endTimeStr != null ? LocalDateTime.parse(endTimeStr) : null;
                    duration = event.optLong("duration", 0);
                }

                UserActivityMonitor activityMonitor = BeanUtils.getBean(UserActivityMonitor.class);

                activityMonitor.saveActivityLog(activityType, startTime, endTime, duration, sessionId, state);
            } catch (Exception e) {
                log.error("Error processing event at index " + i + ": " + e.getMessage());
            }
        }
    }


    // Clear the log file after database insertion
    private static void clearLogFile() throws IOException {
        Files.write(LOG_FILE, new JSONArray().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Initialize system shutdown listener
    public static void initializeSystemShutdownListener() {



        checkShutdownEvent();

        WinUser.WNDCLASSEX wndClass = new WinUser.WNDCLASSEX();
        final String windowClassName = "ShutdownListenerWindow";

        wndClass.lpfnWndProc = new WindowProc();
        wndClass.hInstance = Kernel32.INSTANCE.GetModuleHandle(null);
        wndClass.lpszClassName = windowClassName;

        if (MyUser32.INSTANCE.RegisterClassEx(wndClass).intValue() == 0) {
            log.error("Failed to register window class.");
            return;
        }

        WinDef.HWND hWnd = MyUser32.INSTANCE.CreateWindowEx(0, windowClassName, "Shutdown Listener",
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

        // Unregister session notifications when finished
        Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
    }

    private static void checkShutdownEvent() {
        try {
            // Step 1: Read logs from the file
            JSONArray logs = readLogs();

            if (logs != null && !logs.isEmpty()) {
                // Get the last event from the logs
                JSONObject lastEvent = logs.getJSONObject(logs.length() - 1);
                String activityType = lastEvent.getString("activity_type");

                log.info("Found {} events in the log file.", logs.length());
                log.info("Last event type: {}", activityType);

                // Check if the last event was a system shutdown
                if ("SHUTDOWN".equalsIgnoreCase(activityType)) {
                    log.info("Last event was a system shutdown. Processing logs.");
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

}

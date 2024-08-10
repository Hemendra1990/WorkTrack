package com.hemendra.nrc;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApplicationUsageTracker {

    private String lastActiveWindowTitle = "";
    private LocalDateTime lastActiveTime;
    private Map<String, Long> usageStats = new HashMap<>();

    public static void main(String[] args) {
        ApplicationUsageTracker tracker = new ApplicationUsageTracker();
        tracker.trackApplicationUsage();
    }

    public void trackApplicationUsage() {
        User32 user32 = User32.INSTANCE;
        char[] windowText = new char[512];

        while (true) {
            WinDef.HWND hwnd = user32.GetForegroundWindow();
            user32.GetWindowText(hwnd, windowText, 512);
            String windowTitle = Native.toString(windowText);

            if (!windowTitle.equals(lastActiveWindowTitle)) {
                if (!lastActiveWindowTitle.isEmpty()) {
                    updateUsageStats(lastActiveWindowTitle);
                }
                lastActiveWindowTitle = windowTitle;
                lastActiveTime = LocalDateTime.now();
                System.out.println("Switched to: " + windowTitle + " at " + lastActiveTime);
            }

            try {
                Thread.sleep(1000);  // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUsageStats(String windowTitle) {
        LocalDateTime currentTime = LocalDateTime.now();
        long duration = java.time.Duration.between(lastActiveTime, currentTime).getSeconds();
        usageStats.put(windowTitle, usageStats.getOrDefault(windowTitle, 0L) + duration);
        System.out.println("Application: " + windowTitle + " | Duration: " + duration + " seconds");
    }

    public Map<String, Long> getUsageStats() {
        return usageStats;
    }
}

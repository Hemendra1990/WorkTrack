package com.hemendra.activity.apptracker.trackerimpl;

import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.BrowserTracker;
import com.hemendra.component.WindowsBrowserURLFetcher;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
@Slf4j
public class WindowsAppUsageTracker implements AppUsageTracker, BrowserTracker {
    private static String currentWebsite = "";
    private static Map<String, Long> websiteUsageMap = new HashMap<>();
    private static long startTime = 0;
    private static LocalDateTime startDateTime = LocalDateTime.now();
    private static UUID sessionId = UUID.randomUUID();

    @Autowired
    private WindowsBrowserURLFetcher browserURLFetcher;

    @Override
    public String getActiveWindowTitle() {
        char[] windowText = new char[512];
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        user32.GetWindowText(hwnd, windowText, 512);
        return Native.toString(windowText);
    }

    @Override
    public BufferedImage captureFullDesktop() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    @Override
    public String getBrowserUrl(String activeWindow) {
        String browserName = getBrowserName(activeWindow);
        /*try {
            String[] command = {"powershell", "Get-Process " + browserName + " | ForEach-Object {($_.MainWindowTitle)}"};
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        return browserURLFetcher.fetchBrowserUrlPreservingClipboard();
    }

    private String getBrowserName(String activeWindow) {
        if (activeWindow.toLowerCase().contains("chrome")) {
            return "chrome";
        } else if (activeWindow.toLowerCase().contains("firefox")) {
            return "firefox";
        } else if (activeWindow.toLowerCase().contains("edge")) {
            return "msedge";
        } else if (activeWindow.toLowerCase().contains("safari")) {
            return "safari";
        } else if (activeWindow.toLowerCase().contains("arc")) {
            return "arc";
        } else if (activeWindow.toLowerCase().contains("opera")) {
            return "opera";
        }
        return null;
    }

    @Override
    public void trackWebsiteUsage(String activeWindow, String browserUrl) {
        if (BrowserTracker.isBrowser(activeWindow)) {
            String currentUrl = browserUrl != null ? browserUrl : activeWindow;
            if (!currentWebsite.equals(currentUrl)) {
                if (!currentWebsite.isEmpty()) {
                    long endTime = System.currentTimeMillis();
                    long timeSpent = endTime - startTime;
                    log.info("Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);
                    saveBrowserActivity(activeWindow, currentWebsite, sessionId, startDateTime, LocalDateTime.now(), Duration.between(startDateTime, LocalDateTime.now()).getSeconds());
                }

                // Update the current website and start time
                currentWebsite = currentUrl;
                startTime = System.currentTimeMillis();
                startDateTime = LocalDateTime.now();
                sessionId = UUID.randomUUID();
            }
        } else {
            // If not a browser, reset the tracking
            resetTracking(activeWindow, browserUrl);
        }
    }

    @Override
    public void resetTracking(String activeWindow, String browserUrl) {
        if (!currentWebsite.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - startTime;
            websiteUsageMap.put(currentWebsite,
                    websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);

            log.info("resetTracking: Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);
            if (browserUrl != null) {
                saveBrowserActivity(activeWindow, browserUrl, sessionId, startDateTime, LocalDateTime.now(), Duration.between(startDateTime, LocalDateTime.now()).getSeconds());
            }
            // Reset current website and start time
            currentWebsite = "";
            startTime = 0;
            startDateTime = LocalDateTime.now();
            sessionId = UUID.randomUUID();
        }
    }

}

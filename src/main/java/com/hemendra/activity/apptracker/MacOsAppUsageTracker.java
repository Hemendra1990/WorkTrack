package com.hemendra.activity.apptracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 *
 */
@Component
@Slf4j
public class MacOsAppUsageTracker implements AppUsageTracker, BrowserTracker {

    private static String currentWebsite = "";
    private static Map<String, Long> websiteUsageMap = new HashMap<>();
    private static long startTime = 0;

    @Override
    public String getActiveWindowTitle() {
        String title = "";
        try {
            String[] cmd = {"osascript", "-e", "tell application \"System Events\" to get name of (processes where frontmost is true)"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            title = stdInput.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    @Override
    public BufferedImage captureFullDesktop() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    @Override
    public String getBrowserUrl(String activeWindow) {
        String url = "";
        try {
            //String[] cmd = {"osascript", "-e", "tell application \"Google Chrome\" to get URL of active tab of first window"};
            String[] cmd = {"osascript", "-e", "tell application \"" + activeWindow + "\" to get URL of active tab of first window"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            url = stdInput.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void trackWebsiteUsage(String activeWindow, String browserUrl) {
        if (BrowserTracker.isBrowser(activeWindow)) {
            String currentUrl = browserUrl != null ? browserUrl : activeWindow;

            // Check if the website changed
            if (!currentWebsite.equals(currentUrl)) {
                // Log the time spent on the previous website
                if (!currentWebsite.isEmpty()) {
                    long endTime = System.currentTimeMillis();
                    long timeSpent = endTime - startTime;

                    websiteUsageMap.put(currentWebsite,
                            websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);

                    log.info("Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);
                }

                // Update the current website and start time
                currentWebsite = currentUrl;
                startTime = System.currentTimeMillis();
            }
        } else {
            // If not a browser, reset the tracking
            resetTracking();
        }
    }

    public void resetTracking() {
        if (!currentWebsite.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - startTime;
            websiteUsageMap.put(currentWebsite,
                    websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);

            log.info("Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);

            // Reset current website and start time
            currentWebsite = "";
            startTime = 0;
        }
    }
}

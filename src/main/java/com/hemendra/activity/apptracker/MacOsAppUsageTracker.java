package com.hemendra.activity.apptracker;

import com.hemendra.dto.UserWebsiteActivityDto;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
public class MacOsAppUsageTracker implements AppUsageTracker, BrowserTracker {
    @Autowired
    private WorkTrackUtils workTrackUtils;
    @Autowired
    private WTHttpClient wtHttpClient;

    private static String currentWebsite = "";
    private static Map<String, Long> websiteUsageMap = new HashMap<>();
    private static long startTime = 0;
    private static LocalDateTime startDateTime = LocalDateTime.now();
    private static UUID sessionId = UUID.randomUUID();

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
        String[] cmd = null;
        try {
            if (activeWindow.toLowerCase().contains("firefox")) {
                cmd = new String[]{
                        "osascript",
                        "-e", "tell application \"Firefox\" to activate",
                        "-e", "delay 0.5",
                        "-e", "tell application \"System Events\"",
                        "-e", "keystroke \"l\" using command down",
                        "-e", "keystroke \"c\" using command down",
                        "-e", "end tell",
                        "-e", "delay 0.5",
                        "-e", "set theURL to the clipboard",
                        "-e", "return theURL"
                };
            } else if (activeWindow.toLowerCase().contains("safari")) {
                String appleScript = "tell application \"Safari\"\n" +
                        "    if (count of windows) > 0 then\n" +
                        "        tell front window\n" +
                        "            if (count of tabs) > 0 then\n" +
                        "                set currentTab to current tab\n" +
                        "                set currentURL to URL of currentTab\n" +
                        "            else\n" +
                        "                set currentURL to \"No tabs open\"\n" +
                        "            end if\n" +
                        "        end tell\n" +
                        "    else\n" +
                        "        set currentURL to \"No Safari windows open\"\n" +
                        "    end if\n" +
                        "end tell\n" +
                        "return currentURL";
                cmd = new String[]{"osascript", "-e", appleScript};
            } else {
                cmd = new String[]{"osascript", "-e", "tell application \"" + activeWindow + "\" to get URL of active tab of first window"};
            }
            //String[] cmd = {"osascript", "-e", "tell application \"Google Chrome\" to get URL of active tab of first window"};
            //String[] cmd2 = {"osascript", "-e", "tell application \"" + activeWindow + "\" to get URL of active tab of first window"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            url = stdInput.readLine();
            log.info("Browser url: " + url);
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

                    /*websiteUsageMap.put(currentWebsite,
                            websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);*/
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

    private void saveBrowserActivity(String activeWindow, String browserUrl, UUID sessionId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long duration) {
        String userName = workTrackUtils.getUserName();
        String macAddress = workTrackUtils.getMacAddress();

        UserWebsiteActivityDto userWebsiteActivityDto = new UserWebsiteActivityDto();
        userWebsiteActivityDto.setUserName(userName);
        userWebsiteActivityDto.setMacAddress(macAddress);
        userWebsiteActivityDto.setActivityType(ActivityType.BROWSING);
        userWebsiteActivityDto.setStartTime(startDateTime);
        userWebsiteActivityDto.setEndTime(endDateTime);
        userWebsiteActivityDto.setDuration(duration);
        userWebsiteActivityDto.setSessionId(sessionId);
        userWebsiteActivityDto.setUrl(browserUrl);
        userWebsiteActivityDto.setActiveWindow(activeWindow);

        wtHttpClient.logUserWebsiteActivity(userWebsiteActivityDto);
    }

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

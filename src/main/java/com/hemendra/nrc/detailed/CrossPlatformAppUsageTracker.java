package com.hemendra.nrc.detailed;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CrossPlatformAppUsageTracker {

    private static String currentWebsite = "";
    private static long startTime = 0;
    private static Map<String, Long> websiteUsageMap = new HashMap<>();

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        while (true) {
            String activeWindow = "";

            if (os.contains("win")) {
                activeWindow = getWindowsActiveWindowTitle();
                trackWebsiteUsage(activeWindow, getWindowsBrowserUrl());
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                activeWindow = getLinuxActiveWindowTitle();
                trackWebsiteUsage(activeWindow, getLinuxBrowserUrl());
            } else if (os.contains("mac")) {
                activeWindow = getMacOSActiveWindowTitle();
                trackWebsiteUsage(activeWindow, getMacOSBrowserUrl());
            } else {
                System.out.println("OS not supported.");
            }

            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getWindowsActiveWindowTitle() {
        char[] windowText = new char[512];
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        user32.GetWindowText(hwnd, windowText, 512);
        return Native.toString(windowText);
    }

    public static String getLinuxActiveWindowTitle() {
        String title = "";
        try {
            String[] cmd = {"/bin/sh", "-c", "xprop -root 32x '\t$0' _NET_ACTIVE_WINDOW | cut -f 2"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String windowId = stdInput.readLine().trim();
            if (!windowId.isEmpty()) {
                cmd = new String[]{"/bin/sh", "-c", "xprop -id " + windowId + " _NET_WM_NAME"};
                proc = Runtime.getRuntime().exec(cmd);
                stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                String line;
                while ((line = stdInput.readLine()) != null) {
                    if (line.contains("_NET_WM_NAME")) {
                        title = line.substring(line.indexOf("=") + 2).replace("\"", "");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    public static String getMacOSActiveWindowTitle() {
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

    public static boolean isBrowser(String windowTitle) {
        return windowTitle.toLowerCase().contains("chrome") ||
               windowTitle.toLowerCase().contains("firefox") ||
               windowTitle.toLowerCase().contains("safari") ||
               windowTitle.toLowerCase().contains("edge");
    }

    public static String getWindowsBrowserUrl() {
        String windowTitle = getWindowsActiveWindowTitle();
        return windowTitle; // Typically includes the page title
    }

    public static String getLinuxBrowserUrl() {
        String windowTitle = getLinuxActiveWindowTitle();
        return windowTitle; // Typically includes the page title
    }

    public static String getMacOSBrowserUrl() {
        String url = "";
        try {
            String[] cmd = {"osascript", "-e", "tell application \"Google Chrome\" to get URL of active tab of first window"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            url = stdInput.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void trackWebsiteUsage(String activeWindow, String browserUrl) {
        if (isBrowser(activeWindow)) {
            String currentUrl = browserUrl != null ? browserUrl : activeWindow;

            // Check if the website changed
            if (!currentWebsite.equals(currentUrl)) {
                // Log the time spent on the previous website
                if (!currentWebsite.isEmpty()) {
                    long endTime = System.currentTimeMillis();
                    long timeSpent = endTime - startTime;

                    websiteUsageMap.put(currentWebsite,
                            websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);

                    System.out.println("Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);
                }

                // Update the current website and start time
                currentWebsite = currentUrl;
                startTime = System.currentTimeMillis();
            }
        } else {
            // If not a browser, reset the tracking
            if (!currentWebsite.isEmpty()) {
                long endTime = System.currentTimeMillis();
                long timeSpent = endTime - startTime;

                websiteUsageMap.put(currentWebsite,
                        websiteUsageMap.getOrDefault(currentWebsite, 0L) + timeSpent);

                System.out.println("Spent " + timeSpent / 1000 + " seconds on " + currentWebsite);

                // Reset current website and start time
                currentWebsite = "";
                startTime = 0;
            }
        }
    }
}

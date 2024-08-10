package com.hemendra.activity.browser;

import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.AppUsageTrackerFactory;
import com.hemendra.activity.apptracker.BrowserTracker;
import com.hemendra.activity.apptracker.MacOsAppUsageTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrossPlatformAppUsageTracker {

    private final AppUsageTrackerFactory appUsageTrackerFactory;

    public void runAppUsageTracker() {
        AppUsageTracker appUsageTracker = appUsageTrackerFactory.getOsSpecificAppUsageTracker();

        while (true) {
            String activeWindow = appUsageTracker.getActiveWindowTitle();
            /*log.info("Active window: {}", activeWindow);*/
            //TODO: I am focusing more on MacOS, Later we will be focusing on Windows and Linux
            if (BrowserTracker.isBrowser(activeWindow)) {
                if (appUsageTracker instanceof MacOsAppUsageTracker macOsAppUsageTracker) {
                    String browserUrl = macOsAppUsageTracker.getBrowserUrl(activeWindow);
                    //log.info("Browser URL: {}", browserUrl);
                    macOsAppUsageTracker.trackWebsiteUsage(activeWindow, browserUrl);
                }
            }
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /*public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        while (true) {
            String activeWindow = "";

            if (os.contains("win")) {
                activeWindow = getWindowsActiveWindowTitle();
                if (isBrowser(activeWindow)) {
                    String url = getWindowsBrowserUrl();
                    System.out.println("Browsing: " + url);
                }
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                activeWindow = getLinuxActiveWindowTitle();
                if (isBrowser(activeWindow)) {
                    String url = getLinuxBrowserUrl();
                    System.out.println("Browsing: " + url);
                }
            } else if (os.contains("mac")) {
                activeWindow = getMacOSActiveWindowTitle();
                if (isBrowser(activeWindow)) {
                    String url = getMacOSBrowserUrl(activeWindow);
                    System.out.println("Browsing: " + url);
                }
            } else {
                System.out.println("OS not supported.");
            }

            System.out.println("Active Window: " + activeWindow);

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
                windowTitle.toLowerCase().contains("edge") ||
                windowTitle.toLowerCase().contains("arc") ||
                windowTitle.toLowerCase().contains("opera");
    }

    public static String getWindowsBrowserUrl() {
        // Simplified version: Typically involves complex automation; here we rely on the window title
        String windowTitle = getWindowsActiveWindowTitle();
        return windowTitle; // This usually includes the page title and sometimes the URL
    }

    public static String getLinuxBrowserUrl() {
        // Similar to Windows, may require custom scripts or reliance on window title
        String windowTitle = getLinuxActiveWindowTitle();
        return windowTitle;
    }

    public static String getMacOSBrowserUrl(String activeWindow) {
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
    }*/
}
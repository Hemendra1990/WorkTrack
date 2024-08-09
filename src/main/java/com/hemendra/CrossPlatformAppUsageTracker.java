package com.hemendra;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CrossPlatformAppUsageTracker {

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        while (true) {
            String activeWindow = "";

            if (os.contains("win")) {
                activeWindow = getWindowsActiveWindowTitle();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                activeWindow = getLinuxActiveWindowTitle();
            } else if (os.contains("mac")) {
                activeWindow = getMacOSActiveWindowTitle();
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
}

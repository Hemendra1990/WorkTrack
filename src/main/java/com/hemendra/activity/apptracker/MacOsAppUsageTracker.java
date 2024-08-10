package com.hemendra.activity.apptracker;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class MacOsAppUsageTracker implements AppUsageTracker, BrowserTracker {
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
}

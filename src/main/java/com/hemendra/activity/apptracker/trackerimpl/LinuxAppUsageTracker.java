package com.hemendra.activity.apptracker.trackerimpl;

import com.hemendra.activity.apptracker.AppUsageTracker;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
public class LinuxAppUsageTracker implements AppUsageTracker {
    @Override
    public String getActiveWindowTitle() {
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

    @Override
    public BufferedImage captureFullDesktop() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }
}

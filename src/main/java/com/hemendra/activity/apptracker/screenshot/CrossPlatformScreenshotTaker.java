package com.hemendra.activity.apptracker.screenshot;

import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.AppUsageTrackerFactory;
import com.hemendra.component.WorkTrackProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class CrossPlatformScreenshotTaker {
    private final AppUsageTrackerFactory appUsageTrackerFactory;
    private final WorkTrackProperties workTrackProperties;

    public void runAppScreenshotTaker() throws Exception {
        AppUsageTracker appUsageTracker = appUsageTrackerFactory.getOsSpecificAppUsageTracker();

        while (true) {
            BufferedImage image = appUsageTracker.captureFullDesktop();
            if (image != null) {
                saveScreenshot(image);
            } else {
                log.info("Unable to capture the screen.");
            }
            try {
                Thread.sleep(workTrackProperties.getScreenshotIntervalInMillis()); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage captureFullDesktop() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    private String getMacOSActiveWindowName() throws Exception {
        String[] cmd = {"osascript", "-e", "tell application \"System Events\" to name of application processes whose frontmost is true"};
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = stdInput.readLine();
        return line != null ? line : "Unknown";
    }

    private void saveScreenshot(BufferedImage image) throws Exception {
        String format = "png";
        String fileName = getTimestamp() + "." + format;
        File file = new File("screenshots/" + fileName);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        ImageIO.write(image, format, file);

        log.info("A screenshot was saved as: " + file.getAbsolutePath());
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}


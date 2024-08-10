package com.hemendra.activity.screenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrossPlatformScreenshotTaker {

    public static void main(String[] args) {
        int interval = 60000; // 1 minute interval

        while (true) {
            try {
                takeFullDesktopScreenshotWithActiveWindowInfo();
                Thread.sleep(interval); // Wait for the specified interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void takeFullDesktopScreenshotWithActiveWindowInfo() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            BufferedImage image = null;

            if (osName.contains("win") || osName.contains("nix") || osName.contains("nux")) {
                image = captureFullDesktop();
            } else if (osName.contains("mac")) {
                image = captureFullDesktop();
                String activeWindowName = getMacOSActiveWindowName();
                System.out.println("Active Window: " + activeWindowName);
            }

            if (image != null) {
                saveScreenshot(image);
            } else {
                System.out.println("Unable to capture the screen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage captureFullDesktop() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    private static String getMacOSActiveWindowName() throws Exception {
        String[] cmd = {"osascript", "-e", "tell application \"System Events\" to name of application processes whose frontmost is true"};
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = stdInput.readLine();
        return line != null ? line : "Unknown";
    }

    private static void saveScreenshot(BufferedImage image) throws Exception {
        String format = "png";
        String fileName = getTimestamp() + "." + format;
        File file = new File("screenshots/" + fileName);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        ImageIO.write(image, format, file);

        System.out.println("A screenshot was saved as: " + file.getAbsolutePath());
    }

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}


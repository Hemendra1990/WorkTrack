package com.hemendra.activity.screenshot;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Not WORKING
 */
public class ActiveWindowScreenshot {//TODO: Remove this class as it is not working as expected

    public static void main(String[] args) {
        int interval = 10000; // 1 minute interval

        while (true) {
            takeActiveWindowScreenshot();
            try {
                Thread.sleep(interval); // Wait for the specified interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void takeActiveWindowScreenshot() {
        String os = System.getProperty("os.name").toLowerCase();
        BufferedImage image = null;

        try {
            if (os.contains("win")) {
                image = captureWindowsActiveWindow();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                image = captureLinuxActiveWindow();
            } else if (os.contains("mac")) {
                image = captureMacOSActiveWindow();
            } else {
                System.out.println("OS not supported.");
                return;
            }

            if (image != null) {
                saveScreenshot(image);
            }

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    private static BufferedImage captureWindowsActiveWindow() throws Exception {
        // Windows-specific code to capture the active window
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(hwnd, rect);

        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;

        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(new Rectangle(rect.left, rect.top, width, height));
        return image;
    }

    private static BufferedImage captureLinuxActiveWindow() throws Exception {
        // Linux-specific code to capture the active window
        String[] cmd = {"/bin/sh", "-c", "xwininfo -id $(xprop -root _NET_ACTIVE_WINDOW | cut -d ' ' -f 5)"};
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line;
        int x = 0, y = 0, width = 0, height = 0;
        while ((line = stdInput.readLine()) != null) {
            if (line.contains("Absolute upper-left X:")) {
                x = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains("Absolute upper-left Y:")) {
                y = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains("Width:")) {
                width = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains("Height:")) {
                height = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(new Rectangle(x, y, width, height));
        return image;
    }

    private static BufferedImage captureMacOSActiveWindow() throws Exception {
        // macOS-specific code to capture the active window
        String[] cmd = {"osascript", "-e", "tell application \"System Events\" to get bounds of window 1 of (processes where frontmost is true)"};
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = stdInput.readLine();
        if (line != null && !line.isEmpty()) {
            String[] bounds = line.replaceAll("[^0-9,]", "").split(",");
            int x = Integer.parseInt(bounds[0].trim());
            int y = Integer.parseInt(bounds[1].trim());
            int width = Integer.parseInt(bounds[2].trim()) - x;
            int height = Integer.parseInt(bounds[3].trim()) - y;

            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(new Rectangle(x, y, width, height));
            return image;
        }
        return null;
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

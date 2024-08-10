package com.hemendra.nrc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeriodicScreenshot {

    public static void main(String[] args) {
        int interval = 60000; // 1 minute interval

        while (true) {
            takeScreenshot();
            try {
                Thread.sleep(interval); // Wait for the specified interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void takeScreenshot() {
        try {
            Robot robot = new Robot();
            String format = "png";
            String fileName = getTimestamp() + "." + format;

            // Capture the screen shot of the entire screen
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            File file = new File("screenshots/" + fileName);
            file.getParentFile().mkdirs(); // Create directories if they don't exist
            ImageIO.write(screenFullImage, format, file);

            System.out.println("A screenshot was saved as: " + file.getAbsolutePath());
        } catch (AWTException | java.io.IOException ex) {
            System.err.println(ex);
        }
    }

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}

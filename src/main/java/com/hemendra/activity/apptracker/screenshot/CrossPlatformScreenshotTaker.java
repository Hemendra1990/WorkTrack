package com.hemendra.activity.apptracker.screenshot;

import com.hemendra.activity.apptracker.AppUsageTracker;
import com.hemendra.activity.apptracker.AppUsageTrackerFactory;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private final WTHttpClient wtHttpClient;
    private final WorkTrackUtils workTrackUtils;

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

    private void saveScreenshot(BufferedImage image) throws Exception {
        String format = "jpg";
        String fileName = getTimestamp() + "." + format;
        File file = new File("screenshots/" + fileName);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        //ImageIO.write(image, format, file);
        compressImage(image, file, 0.2f);
        log.info("A screenshot was saved as: " + file.getAbsolutePath());
        wtHttpClient.uploadScreenshot(file, workTrackUtils.getUserName());

    }

    public static void compressImage(BufferedImage image, File outputFile, float quality) throws IOException {
        // Get an ImageWriter for JPEG format
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        // Set compression quality
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);  // 0.0 to 1.0 (low to high quality)
        }

        // Write the compressed image to the output file
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        }

        writer.dispose();
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}


package com.hemendra.activity.apptracker;

import java.awt.image.BufferedImage;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 *
 */
public interface AppUsageTracker {
    String getActiveWindowTitle();

    BufferedImage captureFullDesktop() throws Exception;
}

package com.hemendra.util;

import java.time.LocalDateTime;

public class WtFxUtils {
    private static LocalDateTime appStartTime;

    public static LocalDateTime getAppStartTime() {
        return appStartTime;
    }

    public static void setAppStartTime(LocalDateTime appStartTime) {
        WtFxUtils.appStartTime = appStartTime;
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }
}

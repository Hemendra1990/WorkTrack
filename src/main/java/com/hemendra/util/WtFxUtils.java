package com.hemendra.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static String getTimeFormat(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return localDateTime.format(dateTimeFormatter);
    }

    public static String getFormattedStartTime(LocalDateTime startTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        String formattedDate = startTime.format(dateTimeFormatter);
        return formattedDate;
    }

    public static String getDurationInFormatHHMMSS(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedDuration;
    }
}

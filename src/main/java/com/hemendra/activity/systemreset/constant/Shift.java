package com.hemendra.activity.systemreset.constant;

import java.time.LocalTime;

public enum Shift {
    GENERAL_SHIFT("General Shift [10:00 AM - 7:00 PM]", LocalTime.of(10, 0), LocalTime.of(19, 0)),
    FIRST_SHIFT("First Shift [6:00 AM - 3:00 PM]", LocalTime.of(6, 0), LocalTime.of(15, 0)),
    SECOND_SHIFT("Second Shift [3:00 PM - 12:00 AM]", LocalTime.of(15, 0), LocalTime.of(0, 0)),
    NIGHT_SHIFT("Night Shift [8:00 PM - 5:00 AM]", LocalTime.of(20, 0), LocalTime.of(5, 0)),
    NIGHT_SHIFT_2("Night Shift [10:00 PM - 6:00 AM]", LocalTime.of(22, 0), LocalTime.of(6, 0));

    private final String displayName;
    private final LocalTime startTime;
    private final LocalTime endTime;

    Shift(String displayName, LocalTime startTime, LocalTime endTime) {
        this.displayName = displayName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}

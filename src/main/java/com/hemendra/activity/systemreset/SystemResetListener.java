package com.hemendra.activity.systemreset;

import java.time.LocalDateTime;

public interface SystemResetListener {
    boolean isStartOfDay(LocalDateTime currentEventTime);
    void resetSystemState();
}

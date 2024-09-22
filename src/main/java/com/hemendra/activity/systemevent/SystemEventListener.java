package com.hemendra.activity.systemevent;

public interface SystemEventListener {
    void listenScreenLockEvent();
    void listenScreenUnLockEvent();
    void listenSystemEvent();
    void listenShutdownEvent();

    void systemSleepStartEvent();
    void systemSleepEndEvent();
}

package com.hemendra.activity.systemevent;

public interface SystemEventListener {
    void listenScreenLockOrUnlockEvent();
    void listenSystemEvent();
    void listenShutdownEvent();
}

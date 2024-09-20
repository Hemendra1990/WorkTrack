package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import org.springframework.stereotype.Component;

@Component
public class LinuxSystemEventListener implements SystemEventListener {

    @Override
    public void listenScreenLockOrUnlockEvent() {

    }

    @Override
    public void listenSystemEvent() {

    }

    @Override
    public void listenShutdownEvent() {

    }
}

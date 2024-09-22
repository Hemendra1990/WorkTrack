package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import org.springframework.stereotype.Component;

@Component
public class LinuxSystemEventListener implements SystemEventListener {

    @Override
    public void listenScreenLockEvent() {

    }

    @Override
    public void listenScreenUnLockEvent() {

    }

    @Override
    public void listenSystemEvent() {

    }

    @Override
    public void listenShutdownEvent() {

    }

    @Override
    public void systemSleepStartEvent() {

    }

    @Override
    public void systemSleepEndEvent() {

    }
}

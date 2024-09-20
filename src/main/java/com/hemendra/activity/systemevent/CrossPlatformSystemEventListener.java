package com.hemendra.activity.systemevent;

import com.hemendra.activity.systemevent.factory.SystemEventListenerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrossPlatformSystemEventListener {
    private final SystemEventListenerFactory systemEventListenerFactory;

    public void runSystemEventListener() {
        SystemEventListener osSpecificSystemEventListener = systemEventListenerFactory.getOsSpecificSystemEventListener();
        osSpecificSystemEventListener.listenScreenLockOrUnlockEvent();
    }
}

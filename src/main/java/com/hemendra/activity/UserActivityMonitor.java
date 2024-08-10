package com.hemendra.activity;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.hemendra.component.WorkTrackProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class UserActivityMonitor implements NativeKeyListener, NativeMouseListener {

    @Autowired
    WorkTrackProperties workTrackProperties;

    public UserActivityMonitor() {
        this.lastActivityTime = LocalDateTime.now();
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
    }


    private LocalDateTime lastActivityTime;
    private LocalDateTime idleStartTime;
    private boolean isIdle = false;


    public void startMonitoring() {
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);

            // Monitor idle and active state in an infinite loop
            while (true) {
                this.checkIdleTime();
                try {
                    Thread.sleep(1000);  // Check every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkIdleTime() {
        log.info("Checking idle time {} - {}", Thread.currentThread().getName(), lastActivityTime);
        LocalDateTime currentTime = LocalDateTime.now();
        long idleDuration = java.time.Duration.between(this.lastActivityTime, currentTime).getSeconds();

        if (!isIdle && idleDuration >= workTrackProperties.getIdleThresholdSeconds()) {
            // Transition from active to idle
            isIdle = true;
            idleStartTime = currentTime;
            System.out.println("User went idle at: " + idleStartTime);
        } else if (isIdle && idleDuration < workTrackProperties.getIdleThresholdSeconds()) {
            // Transition from idle to active
            isIdle = false;
            System.out.println("User resumed activity at: " + currentTime);
            System.out.println("User was idle for: " + idleDuration + " seconds.");
            lastActivityTime = currentTime;  // Reset last activity time
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        updateLastActivityTime();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        updateLastActivityTime();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        updateLastActivityTime();
    }

    private void updateLastActivityTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        if (isIdle) {
            System.out.println("User resumed activity at: " + currentTime);
            long idleDuration = java.time.Duration.between(idleStartTime, currentTime).getSeconds();
            System.out.println("User was idle for: " + idleDuration + " seconds.");
            isIdle = false;
        }
        lastActivityTime = currentTime;
    }

    // Implement other necessary methods from the NativeKeyListener and NativeMouseListener interfaces
}

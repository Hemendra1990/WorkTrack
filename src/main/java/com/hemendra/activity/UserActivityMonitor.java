package com.hemendra.activity;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class UserActivityMonitor implements NativeKeyListener, NativeMouseListener {

    private LocalDateTime lastActivityTime;
    private LocalDateTime idleStartTime;
    private boolean isIdle = false;
    private static final int IDLE_THRESHOLD_SECONDS = 10;  // For testing, set it to 10 seconds

    public UserActivityMonitor() {
        lastActivityTime = LocalDateTime.now();
    }

    public static void main(String[] args) {
        // Disable logging from JNativeHook
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(java.util.logging.Level.OFF);

        UserActivityMonitor monitor = new UserActivityMonitor();

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(monitor);
        GlobalScreen.addNativeMouseListener(monitor);

        // Monitor idle and active state in an infinite loop
        while (true) {
            monitor.checkIdleTime();
            try {
                Thread.sleep(1000);  // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIdleTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        long idleDuration = java.time.Duration.between(lastActivityTime, currentTime).getSeconds();

        if (!isIdle && idleDuration >= IDLE_THRESHOLD_SECONDS) {
            // Transition from active to idle
            isIdle = true;
            idleStartTime = currentTime;
            System.out.println("User went idle at: " + idleStartTime);
        } else if (isIdle && idleDuration < IDLE_THRESHOLD_SECONDS) {
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

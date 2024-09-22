package com.hemendra.activity.systemevent.screenlock.win;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WindowsScreenLockDetector {

    @Setter
    private WinDef.HWND hWnd;

    @Setter
    private SystemEventListener listener;
    private static WindowsScreenLockDetector instance;

    private WindowsScreenLockDetector() {
    }

    public static synchronized WindowsScreenLockDetector getInstance() {
        if (instance == null) {
            instance = new WindowsScreenLockDetector();
        }
        return instance;
    }

    public void startDetection() {
        listener.listenSystemEvent();
    }

    public void stopDetection() {
        if (hWnd != null) {
            Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
            User32.INSTANCE.DestroyWindow(hWnd);
            hWnd = null;
        }
    }
}

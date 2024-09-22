package com.hemendra.activity.systemevent.screenlock.win;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

public interface Wtsapi32 extends StdCallLibrary {
    Wtsapi32 INSTANCE = Native.load("Wtsapi32", Wtsapi32.class);

    boolean WTSRegisterSessionNotification(HWND hWnd, int dwFlags);

    boolean WTSUnRegisterSessionNotification(HWND hWnd);
}
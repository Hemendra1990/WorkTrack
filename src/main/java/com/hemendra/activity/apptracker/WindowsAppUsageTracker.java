package com.hemendra.activity.apptracker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.springframework.stereotype.Component;

@Component
public class WindowsAppUsageTracker implements AppUsageTracker {
    @Override
    public String getActiveWindowTitle() {
        char[] windowText = new char[512];
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        user32.GetWindowText(hwnd, windowText, 512);
        return Native.toString(windowText);
    }
}

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
    private static final int NOTIFY_FOR_THIS_SESSION = 0;
    private static final int WM_WTSSESSION_CHANGE = 0x02B1;
    private static final int WTS_SESSION_LOCK = 0x7;
    private static final int WTS_SESSION_UNLOCK = 0x8;

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
        if (!Platform.isWindows()) {
            throw new UnsupportedOperationException("This implementation only supports Windows.");
        }

        String className = "ScreenLockDetectorClass";
        WinUser.WNDCLASSEX wClass = new WinUser.WNDCLASSEX();
        wClass.hInstance = null;
        wClass.lpfnWndProc = new WinUser.WindowProc() {
            @Override
            public WinDef.LRESULT callback(WinDef.HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
                switch (uMsg) {
                    case WM_WTSSESSION_CHANGE:
                        if (wParam.intValue() == WTS_SESSION_LOCK) {
                            listener.listenScreenLockEvent();
                        } else if (wParam.intValue() == WTS_SESSION_UNLOCK) {
                            listener.listenScreenUnLockEvent();
                        }
                        return new WinDef.LRESULT(0);
                    default:
                        return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
                }
            }
        };
        wClass.lpszClassName = className;
        User32.INSTANCE.RegisterClassEx(wClass);
        hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, className,
                "ScreenLockDetector", 0, 0, 0, 0, 0,
                null, null, null, null);

        if (hWnd == null) {
            throw new RuntimeException("Failed to create window");
        }

        if (!Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, NOTIFY_FOR_THIS_SESSION)) {
            throw new RuntimeException("Failed to register for session notifications");
        }

        WinUser.MSG msg = new WinUser.MSG();
        while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }
    }

    public void stopDetection() {
        if (hWnd != null) {
            Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
            User32.INSTANCE.DestroyWindow(hWnd);
            hWnd = null;
        }
    }
}

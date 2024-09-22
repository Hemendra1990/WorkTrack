package com.hemendra.win;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.win32.StdCallLibrary;

public class ScreenLockDetector {

    public interface Wtsapi32 extends StdCallLibrary {
        Wtsapi32 INSTANCE = Native.load("Wtsapi32", Wtsapi32.class);

        boolean WTSRegisterSessionNotification(HWND hWnd, int dwFlags);

        boolean WTSUnRegisterSessionNotification(HWND hWnd);
    }

    private static final int NOTIFY_FOR_THIS_SESSION = 0;
    private static final int WM_WTSSESSION_CHANGE = 0x02B1;
    private static final int WTS_SESSION_LOCK = 0x7;
    private static final int WTS_SESSION_UNLOCK = 0x8;

    private HWND hWnd;
    private final ScreenLockListener listener;

    public interface ScreenLockListener {
        void onScreenLocked();

        void onScreenUnlocked();
    }

    public ScreenLockDetector(ScreenLockListener listener) {
        this.listener = listener;
    }

    public void startDetection() {
        if (!Platform.isWindows()) {
            throw new UnsupportedOperationException("This implementation only supports Windows.");
        }

        String className = "ScreenLockDetectorClass";
        WNDCLASSEX wClass = new WNDCLASSEX();
        wClass.hInstance = null;
        wClass.lpfnWndProc = new WindowProc() {
            @Override
            public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
                switch (uMsg) {
                    case WM_WTSSESSION_CHANGE:
                        if (wParam.intValue() == WTS_SESSION_LOCK) {
                            listener.onScreenLocked();
                        } else if (wParam.intValue() == WTS_SESSION_UNLOCK) {
                            listener.onScreenUnlocked();
                        }
                        return new LRESULT(0);
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

        MSG msg = new MSG();
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

    public static void main(String[] args) {
        ScreenLockDetector detector = new ScreenLockDetector(new ScreenLockListener() {
            @Override
            public void onScreenLocked() {
                System.out.println("Screen locked");
            }

            @Override
            public void onScreenUnlocked() {
                System.out.println("Screen unlocked");
            }
        });

        detector.startDetection();
    }
}
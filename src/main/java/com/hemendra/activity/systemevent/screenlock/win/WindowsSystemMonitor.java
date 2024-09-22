package com.hemendra.activity.systemevent.screenlock.win;

import com.hemendra.activity.systemevent.impl.WindowsSystemEventListener;
import com.hemendra.util.BeanUtils;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class WindowsSystemMonitor implements WinUser.WindowProc {
    private static final int WM_QUERYENDSESSION = 0x0011;
    private static final int WM_ENDSESSION = 0x0016;
    private static final int WM_POWERBROADCAST = 0x0218;
    private static final int PBT_APMSUSPEND = 0x0004; // System is suspending (sleep)
    private static final int PBT_APMRESUMESUSPEND = 0x0007; // System is resuming from sleep
    private static final int WM_WTSSESSION_CHANGE = 0x02B1;  // Session change
    private static final int WTS_SESSION_LOCK = 0x7;         // Session is locked
    private static final int WTS_SESSION_UNLOCK = 0x8;
    private static final int WTS_CONSOLE_CONNECT = 0x1;      // Console connected
    private static final int WTS_CONSOLE_DISCONNECT = 0x2;   // Console disconnected
    private static final int WTS_REMOTE_CONNECT = 0x3;       // Remote connected
    private static final int WTS_REMOTE_DISCONNECT = 0x4;    // Remote disconnected
    private static final int WTS_SESSION_LOGON = 0x5;        // Session logon
    private static final int WTS_SESSION_LOGOFF = 0x6;       // Session logoff

    @Override
    public WinDef.LRESULT callback(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam) {
        WindowsSystemEventListener listener = BeanUtils.getBean(WindowsSystemEventListener.class);

        log.info("Received Windows event. uMsg: {}, uParam: {}, lParam: {}", uMsg, uParam.intValue(), lParam);

        switch (uMsg) {

            case WM_QUERYENDSESSION:
                log.info("WM_QUERYENDSESSION: System shutdown event detected at {}", LocalDateTime.now().toString());
                listener.listenShutdownEvent();
                return new WinDef.LRESULT(1);

            case WM_POWERBROADCAST:
                log.info("WM_POWERBROADCAST: Power event detected. uParam: {}", uParam.intValue());
                if (uParam.intValue() == PBT_APMSUSPEND) {
                    log.info("PBT_APMSUSPEND: System sleep start detected at {}", LocalDateTime.now().toString());
                    listener.systemSleepStartEvent();
                } else if (uParam.intValue() == PBT_APMRESUMESUSPEND) {
                    log.info("PBT_APMRESUMESUSPEND: System wake from sleep detected at {}",LocalDateTime.now().toString());
                    listener.systemSleepEndEvent();
                }
                break;

            case WM_WTSSESSION_CHANGE:
                log.info("WM_WTSSESSION_CHANGE: Session change event detected. uParam: {}", uParam.intValue());
                switch (uParam.intValue()) {
                    case WTS_SESSION_LOCK:
                        log.info("WTS_SESSION_LOCK: Screen lock event detected at {}",LocalDateTime.now().toString());
                        listener.listenScreenLockEvent();
                        break;
                    case WTS_SESSION_UNLOCK:
                        log.info("WTS_SESSION_UNLOCK: Screen unlock event detected at {}",LocalDateTime.now().toString());
                        listener.listenScreenUnLockEvent();
                        break;
                    case WTS_CONSOLE_CONNECT:
                        log.info("WTS_CONSOLE_CONNECT: Console connect event at {}", LocalDateTime.now().toString());
                        // Handle console connect
                        break;
                    case WTS_CONSOLE_DISCONNECT:
                        log.info("WTS_CONSOLE_DISCONNECT: Console disconnect event at {}",LocalDateTime.now().toString());
                        // Handle console disconnect
                        break;
                    case WTS_REMOTE_CONNECT:
                        log.info("WTS_REMOTE_CONNECT: Remote desktop connect event at {}", LocalDateTime.now().toString());
                        // Handle remote connect
                        break;
                    case WTS_REMOTE_DISCONNECT:
                        log.info("WTS_REMOTE_DISCONNECT: Remote desktop disconnect event at {}",LocalDateTime.now().toString());
                        // Handle remote disconnect
                        break;
                    case WTS_SESSION_LOGON:
                        log.info("WTS_SESSION_LOGON: User logged on event at {}", LocalDateTime.now().toString());
                        // Handle logon
                        break;
                    case WTS_SESSION_LOGOFF:
                        log.info("WTS_SESSION_LOGOFF: User logged off event at {}", LocalDateTime.now().toString());
                        // Handle logoff
                        break;
                    default:
                        log.info("Unhandled session change event: {}", uParam.intValue());
                        break;
                }
                break;

            default:
                return MyUser32.INSTANCE.DefWindowProc(hWnd, uMsg, uParam, lParam);
        }
        return new WinDef.LRESULT(1);
    }
}

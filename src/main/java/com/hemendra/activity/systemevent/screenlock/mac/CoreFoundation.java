package com.hemendra.activity.systemevent.screenlock.mac;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface CoreFoundation extends Library {

    class CFStringRef extends PointerType {}
    class CFNotificationCenterRef extends PointerType {}
    class CFRunLoopRef extends PointerType {}
    CFNotificationCenterRef CFNotificationCenterGetDistributedCenter();
    void CFNotificationCenterAddObserver(CFNotificationCenterRef center, Pointer observer,
                                         CFNotificationCallback callBack, CFStringRef name,
                                         Pointer object, int suspensionBehavior);
    CFStringRef CFStringCreateWithCString(Pointer alloc, String cStr, int encoding);
    void CFRelease(Pointer obj);
    CFRunLoopRef CFRunLoopGetCurrent();
    void CFRunLoopRun();
    int CFRunLoopRunInMode(CFStringRef mode, double seconds, boolean returnAfterSourceHandled);
    long CFStringGetLength(CFStringRef theString);
    Pointer CFStringGetCStringPtr(CFStringRef theString, int encoding);
    boolean CFStringGetCString(CFStringRef theString, byte[] buffer, long bufferSize, int encoding);
}

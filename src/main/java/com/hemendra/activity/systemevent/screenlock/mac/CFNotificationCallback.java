package com.hemendra.activity.systemevent.screenlock.mac;

import com.sun.jna.Pointer;

@FunctionalInterface
public interface CFNotificationCallback extends com.sun.jna.Callback {
    void invoke(CoreFoundation.CFNotificationCenterRef center, Pointer observer,
                CoreFoundation.CFStringRef name, Pointer object, Pointer userInfo);
}

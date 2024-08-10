package com.hemendra.activity.apptracker;

import com.hemendra.component.WorkTrackProperties;
import com.hemendra.util.BeanUtils;

public interface BrowserTracker {
    String getBrowserUrl(String activeWindow);

    void trackWebsiteUsage(String activeWindow, String browserUrl);

    static boolean isBrowser(String windowTitle) {
        WorkTrackProperties properties = BeanUtils.getBean(WorkTrackProperties.class);
        return properties.getMonitoringBrowsers().stream().filter(browser -> windowTitle.toLowerCase().contains(browser.toLowerCase())).count() > 0;
    }
}

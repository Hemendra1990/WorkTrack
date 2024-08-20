package com.hemendra.activity.apptracker;

import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.UserWebsiteActivityDto;
import com.hemendra.enums.ActivityType;
import com.hemendra.http.WTHttpClient;
import com.hemendra.util.BeanUtils;
import com.hemendra.util.WorkTrackUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
public interface BrowserTracker {
    String getBrowserUrl(String activeWindow);

    void trackWebsiteUsage(String activeWindow, String browserUrl);

    void resetTracking(String activeWindow, String browserUrl);

    static boolean isBrowser(String windowTitle) {
        WorkTrackProperties properties = BeanUtils.getBean(WorkTrackProperties.class);
        return properties.getMonitoringBrowsers().stream().filter(browser -> windowTitle.toLowerCase().contains(browser.toLowerCase())).count() > 0;
    }

    default void saveBrowserActivity(String activeWindow, String browserUrl, UUID sessionId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long duration) {
        WTHttpClient wtHttpClient = BeanUtils.getBean(WTHttpClient.class);
        WorkTrackUtils workTrackUtils = BeanUtils.getBean(WorkTrackUtils.class);
        String userName = workTrackUtils.getUserName();
        String macAddress = workTrackUtils.getMacAddress();

        UserWebsiteActivityDto userWebsiteActivityDto = new UserWebsiteActivityDto();
        userWebsiteActivityDto.setUserName(userName);
        userWebsiteActivityDto.setMacAddress(macAddress);
        userWebsiteActivityDto.setActivityType(ActivityType.BROWSING);
        userWebsiteActivityDto.setStartTime(startDateTime);
        userWebsiteActivityDto.setEndTime(endDateTime);
        userWebsiteActivityDto.setDuration(duration);
        userWebsiteActivityDto.setSessionId(sessionId);
        userWebsiteActivityDto.setUrl(browserUrl);
        userWebsiteActivityDto.setActiveWindow(activeWindow);

        wtHttpClient.logUserWebsiteActivity(userWebsiteActivityDto);
    }
}

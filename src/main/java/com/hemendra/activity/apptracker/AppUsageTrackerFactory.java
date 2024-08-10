package com.hemendra.activity.apptracker;

import com.hemendra.util.BeanUtils;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppUsageTrackerFactory implements InitializingBean {
    private final WorkTrackUtils workTrackUtils;

    private String os;

    @Override
    public void afterPropertiesSet() throws Exception {
        os = workTrackUtils.getOsName();
    }

    public AppUsageTracker getOsSpecificAppUsageTracker() {
        if (os.contains("win")) {
            return BeanUtils.getBean(WindowsAppUsageTracker.class);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return BeanUtils.getBean(LinuxAppUsageTracker.class);
        } else if (os.contains("mac")) {
            return BeanUtils.getBean(MacOsAppUsageTracker.class);
        } else {
            log.error("OS not supported.");
            return null;
        }
    }


}

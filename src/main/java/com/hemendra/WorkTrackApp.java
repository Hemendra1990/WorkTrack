package com.hemendra;

import com.hemendra.activity.UserActivityMonitor;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.config.WorkTrackConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class WorkTrackApp {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        AnnotationConfigApplicationContext workTrackAppContext = new AnnotationConfigApplicationContext();
        workTrackAppContext.register(WorkTrackConfig.class);
        workTrackAppContext.refresh();

        WorkTrackProperties workTrackProperties = workTrackAppContext.getBean(WorkTrackProperties.class);
        log.info("Staritng " + workTrackProperties.getAppName() + " version " + workTrackProperties.getAppVersion());


        UserActivityMonitor activityMonitor = workTrackAppContext.getBean(UserActivityMonitor.class);
        activityMonitor.startMonitoring();
    }
}

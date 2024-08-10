package com.hemendra;

import com.hemendra.component.TextComp;
import com.hemendra.config.WorkTrackConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class WorkTrackApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext workTrackAppContext = new AnnotationConfigApplicationContext();
        workTrackAppContext.register(WorkTrackConfig.class);
        workTrackAppContext.refresh();

        UserActivityMonitor activityMonitor = workTrackAppContext.getBean(UserActivityMonitor.class);
        System.out.println(activityMonitor);

        String text = workTrackAppContext.getBean(TextComp.class).getText();
        System.out.println(text);
    }
}

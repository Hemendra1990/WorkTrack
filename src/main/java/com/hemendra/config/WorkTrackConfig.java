package com.hemendra.config;

import com.hemendra.activity.UserActivityMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:worktrack-app.properties")
@Configuration
@ComponentScan("com.hemendra")
public class WorkTrackConfig {

    @Bean
    public UserActivityMonitor userActivityMonitor() {
        return new UserActivityMonitor();
    }
}

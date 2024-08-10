package com.hemendra.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:worktrack-app.properties")
@Configuration
@ComponentScan("com.hemendra.*")
public class WorkTrackConfig {
}

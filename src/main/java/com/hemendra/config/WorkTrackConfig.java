package com.hemendra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@PropertySource("classpath:worktrack-app.properties")
@Configuration
@ComponentScan("com.hemendra.*")
public class WorkTrackConfig {
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }
}

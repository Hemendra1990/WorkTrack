package com.hemendra.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppActivityDto implements Serializable {

    private static final long serialVersionUID = 202408240225210L;
    Long id;
    String userName;
    String macAddress;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Long duration;
    String activeWindow;
    String appCategory;
    String appName;
}

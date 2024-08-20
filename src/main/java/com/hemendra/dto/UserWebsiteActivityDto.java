package com.hemendra.dto;

import com.hemendra.enums.ActivityType;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
public class UserWebsiteActivityDto {
    Long id;
    String userName;
    String macAddress;
    ActivityType activityType;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Long duration;
    UUID sessionId;
    String url;
    String activeWindow;
}
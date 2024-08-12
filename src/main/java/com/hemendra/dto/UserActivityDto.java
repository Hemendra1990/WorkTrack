package com.hemendra.dto;

import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserActivityDto {
    Long id;
    String userName;
    String macAddress;
    ActivityType activityType;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Long duration;
    UUID sessionId;
    ActivityState state;
}
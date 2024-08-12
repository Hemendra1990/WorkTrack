package com.hemendra.enums;

public enum ActivityType {
    ACTIVE("ACTIVE"),
    IDLE("IDLE");

    private String type;

    private ActivityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

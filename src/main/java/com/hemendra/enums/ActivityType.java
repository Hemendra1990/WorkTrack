package com.hemendra.enums;

public enum ActivityType {
    ACTIVE("ACTIVE"),
    STARTED("STARTED"),
    IDLE("IDLE"),
    BROWSING("BROWSING");

    private String type;

    private ActivityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

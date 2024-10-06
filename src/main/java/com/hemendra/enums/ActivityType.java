package com.hemendra.enums;

public enum ActivityType {
    ACTIVE("ACTIVE"),
    STARTED("STARTED"),
    IDLE("IDLE"),
    BROWSING("BROWSING"),
    SHUTDOWN("SHUTDOWN"),
    LOCK("LOCK"),
    SLEEP("SLEEP"),
    RESTART("RESTART");

    private String type;

    private ActivityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

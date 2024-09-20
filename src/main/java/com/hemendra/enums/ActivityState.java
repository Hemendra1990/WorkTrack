package com.hemendra.enums;

public enum ActivityState {
    START("start"),
    END("end"),
    AWAY("away"),
    CONTINUE("continue"),
    SHUTDOWN("shutdown"),
    LOCK_COMPLETE("lock_complete"),
    SLEEP_COMPLETE("sleep_complete");

    private String state;

    ActivityState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

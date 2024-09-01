package com.hemendra.enums;

public enum ActivityState {
    START("start"),
    END("end"),
    AWAY("away"),
    CONTINUE("continue");

    private String state;

    ActivityState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

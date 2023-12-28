package com.pronovoscm.model;

public enum SubmittalStatusEnum {
    All(-1, "All"),
    Draft(0, "Draft"),
    Open(1, "Open"),
    Closed(2, "Closed");


    final String statusString;
    private final int statusValue;

    SubmittalStatusEnum(int value, String s) {
        statusValue = value;
        statusString = s;
    }

    public int getStatusValue() {
        return this.statusValue;
    }

    public String getStatusString() {
        return this.statusString;
    }
}

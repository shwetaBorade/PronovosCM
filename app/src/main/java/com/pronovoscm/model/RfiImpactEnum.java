package com.pronovoscm.model;

public enum RfiImpactEnum {
    TBD(0, "TBD"),
    Yes(1, "Yes"),
    No(2, "No");
    private final int statusValue;

    final String statusString;

    RfiImpactEnum(int value, String impactValue) {
        statusValue = value;
        statusString = impactValue;
    }

    public int getStatusValue() {
        return this.statusValue;
    }
}

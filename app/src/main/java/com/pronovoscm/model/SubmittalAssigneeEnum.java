package com.pronovoscm.model;

public enum SubmittalAssigneeEnum {
    NoResponse(0, "No response"),
    Approved(1, "Approved"),
    Rejected(2, "Rejected"),
    ApprovedAsNoted(3, "Approved as Noted"),
    Reviewed(4, "Reviewed"),
    ReviewedAsNoted(5, "Reviewed as Noted"),
    ReviseAndResubmit(6, "Revise and Resubmit"),
    Other(7, "Other");


    final String statusString;
    private final int statusValue;

    SubmittalAssigneeEnum(int value, String s) {
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

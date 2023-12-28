package com.pronovoscm.model;

/**
 * Created on 26/11/18.
 *
 * @author GWL
 */
public enum PunchListStatus {
    Open(1), Complete(2),Approved(3), Rejected(4), All(-1);


    private final int value;

    PunchListStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * To get status entity associated with int value
     *
     * @param value
     * @return Status.Entity
     */
    public static PunchListStatus getStatus(int value) {
        for (PunchListStatus status : PunchListStatus.values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Status not found.");
    }
}

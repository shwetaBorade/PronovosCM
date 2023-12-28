package com.pronovoscm.utils;

import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.persistence.domain.PunchlistAssignee;

public class PunchListFilterEvent {

    private PunchListStatus mPunchListStatus;
    private PunchlistAssignee mPunchlistAssignee;

    public PunchListFilterEvent(PunchListStatus punchListStatus, PunchlistAssignee punchlistAssignee) {
        mPunchListStatus = punchListStatus;
        mPunchlistAssignee = punchlistAssignee;
    }

    public PunchListStatus getPunchListStatus() {
        return mPunchListStatus;
    }

    public PunchlistAssignee getPunchlistAssignee() {
        return mPunchlistAssignee;
    }
}

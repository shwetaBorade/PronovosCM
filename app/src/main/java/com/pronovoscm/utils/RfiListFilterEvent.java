package com.pronovoscm.utils;

import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.persistence.domain.PjRfiContactList;

import java.util.Date;

public class RfiListFilterEvent {

    private RFIStatusEnum mRfiStatusEnum;
    private PjRfiContactList mPjRfiAssignee;
    private Date dueDate;
    private Date submittedDate;

    public RfiListFilterEvent(RFIStatusEnum rfiStatusEnum, PjRfiContactList punchlistAssignee, Date dueDate, Date dateSubmitted) {
        mRfiStatusEnum = rfiStatusEnum;
        this.dueDate = dueDate;
        this.submittedDate = dateSubmitted;
        mPjRfiAssignee = punchlistAssignee;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public RFIStatusEnum getRfiStatusEnum() {
        return mRfiStatusEnum;
    }

    public PjRfiContactList getPjRfiContactList() {
        return mPjRfiAssignee;
    }

    @Override
    public String toString() {
        return "RfiListFilterEvent{" +
                "mRfiStatusEnum=" + mRfiStatusEnum +
                ", mPunchlistAssignee=" + mPjRfiAssignee +
                ", dueDate=" + dueDate +
                ", submittedDate=" + submittedDate +
                '}';
    }
}

package com.pronovoscm.model;

import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittals;

public class SubmittalListItem {

    public PjSubmittals pjSubmittals;
    public int contactCount;

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public PjSubmittalContactList pjSubmittalContactList;

    public SubmittalListItem() {
    }

    public PjSubmittals getPjSubmittals() {
        return pjSubmittals;
    }

    public void setPjSubmittals(PjSubmittals pjSubmittals) {
        this.pjSubmittals = pjSubmittals;
    }

    public PjSubmittalContactList getPjSubmittalContactList() {
        return pjSubmittalContactList;
    }

    public void setPjSubmittalContactList(PjSubmittalContactList pjSubmittalContactList) {
        this.pjSubmittalContactList = pjSubmittalContactList;
    }
}

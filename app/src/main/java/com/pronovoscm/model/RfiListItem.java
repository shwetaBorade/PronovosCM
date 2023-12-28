package com.pronovoscm.model;

import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiContactList;

public class RfiListItem {
    public PjRfi pjRfi;
    public PjRfiContactList pjRfiContactList;

    public RfiListItem() {

    }

    public PjRfi getPjRfi() {
        return pjRfi;
    }

    public void setPjRfi(PjRfi pjRfi) {
        this.pjRfi = pjRfi;
    }

    public PjRfiContactList getPjRfiContactList() {
        return pjRfiContactList;
    }

    public void setPjRfiContactList(PjRfiContactList pjRfiContactList) {
        this.pjRfiContactList = pjRfiContactList;
    }
}

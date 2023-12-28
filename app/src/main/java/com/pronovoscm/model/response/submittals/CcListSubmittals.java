package com.pronovoscm.model.response.submittals;

import com.google.gson.annotations.SerializedName;

public class CcListSubmittals {
    @SerializedName("pj_submittal_contact_list_id")
    private int pjSubmittalContactListId;
    @SerializedName("pj_submittals_id")
    private int pjSubmittalsId;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("deleted_at")
    private String deletedAt;

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getPjSubmittalContactListId() {
        return pjSubmittalContactListId;
    }

    public void setPjSubmittalContactListId(int pjSubmittalContactListId) {
        this.pjSubmittalContactListId = pjSubmittalContactListId;
    }

    public int getPjSubmittalsId() {
        return pjSubmittalsId;
    }

    public void setPjSubmittalsId(int pjSubmittalsId) {
        this.pjSubmittalsId = pjSubmittalsId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}

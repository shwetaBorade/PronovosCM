package com.pronovoscm.model.request.sendemail;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendEmailData {
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("cc_list")
    private List<CcList> ccList;

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<CcList> getCcList() {
        return ccList;
    }

    public void setCcList(List<CcList> ccList) {
        this.ccList = ccList;
    }
}

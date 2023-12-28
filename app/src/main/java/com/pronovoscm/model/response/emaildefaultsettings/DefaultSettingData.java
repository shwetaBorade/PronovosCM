package com.pronovoscm.model.response.emaildefaultsettings;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.cclist.Cclist;

import java.util.List;

public class DefaultSettingData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("cc_list")
    private List<Cclist> ccList;
    @SerializedName("assigned_to")
    private String assignedTo;

    public String getResponsemsg() {
        return responsemsg;
    }

    public void setResponsemsg(String responsemsg) {
        this.responsemsg = responsemsg;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public List<Cclist> getCcList() {
        return ccList;
    }

    public void setCcList(List<Cclist> ccList) {
        this.ccList = ccList;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}

package com.pronovoscm.model.response.projectsubcontractors;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubcontractorData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("subcontractors")
    private List<Subcontractors> subcontractors;

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

    public List<Subcontractors> getSubcontractors() {
        return subcontractors;
    }

    public void setSubcontractors(List<Subcontractors> subcontractors) {
        this.subcontractors = subcontractors;
    }
}

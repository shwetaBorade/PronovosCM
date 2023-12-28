package com.pronovoscm.model.response.cclist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CCListData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("cclist")
    private List<Cclist> cclist;

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

    public List<Cclist> getCclist() {
        return cclist;
    }

    public void setCclist(List<Cclist> cclist) {
        this.cclist = cclist;
    }
}

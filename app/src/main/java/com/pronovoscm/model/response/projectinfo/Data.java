package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("info")
    private Info info;

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

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}

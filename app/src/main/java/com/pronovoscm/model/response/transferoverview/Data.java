package com.pronovoscm.model.response.transferoverview;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("transfers")
    private Transfers transfers;

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

    public Transfers getTransfers() {
        return transfers;
    }

    public void setTransfers(Transfers transfers) {
        this.transfers = transfers;
    }
}

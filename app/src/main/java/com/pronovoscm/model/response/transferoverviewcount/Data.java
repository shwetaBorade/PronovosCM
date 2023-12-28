package com.pronovoscm.model.response.transferoverviewcount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Data {

    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("transfer_count")
    private List<TransferCount> transferCount;

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

    public List<TransferCount> getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(List<TransferCount> transferCount) {
        this.transferCount = transferCount;
    }
}

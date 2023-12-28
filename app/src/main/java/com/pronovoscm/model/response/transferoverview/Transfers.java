package com.pronovoscm.model.response.transferoverview;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transfers {
    @SerializedName("title")
    private String title;
    @SerializedName("transfer_data")
    private List<TransferData> transferData;
    @SerializedName("count")
    private int count;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TransferData> getTransferData() {
        return transferData;
    }

    public void setTransferData(List<TransferData> transferData) {
        this.transferData = transferData;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

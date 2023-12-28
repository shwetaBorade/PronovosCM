package com.pronovoscm.model.response.trades;

import com.google.gson.annotations.SerializedName;

public class TradesResponse {

    @SerializedName("data")
    private TradesData mTradesData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public TradesData getData() {
        return mTradesData;
    }

    public void setData(TradesData data) {
        this.mTradesData = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

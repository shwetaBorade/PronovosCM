package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

public class PunchListHistoryResponse {
    @SerializedName("data")
    private PunchListHistoryData data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public PunchListHistoryData getData() {
        return data;
    }

    public void setData(PunchListHistoryData data) {
        this.data = data;
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

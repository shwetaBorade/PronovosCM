package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

public class PunchListResponse {

    @SerializedName("data")
    private PunchListData data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public void setData(PunchListData data) {
        this.data = data;
    }

    public PunchListData getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PunchListResponse{" +
                "data = '" + data + '\'' +
                ",message = '" + message + '\'' +
                ",status = '" + status + '\'' +
                "}";
    }
}
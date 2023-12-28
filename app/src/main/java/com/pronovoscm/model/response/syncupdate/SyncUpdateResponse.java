package com.pronovoscm.model.response.syncupdate;

import com.google.gson.annotations.SerializedName;

public class SyncUpdateResponse {

    @SerializedName("data")
    private SyncUpdateData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public SyncUpdateData getData() {
        return data;
    }

    public void setData(SyncUpdateData data) {
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

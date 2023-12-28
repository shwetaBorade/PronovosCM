package com.pronovoscm.model.response.punchlistemail;

import com.google.gson.annotations.SerializedName;

public class PunchListEmailResponse {

    @SerializedName("data")
    private PunchlistEmailData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public PunchlistEmailData getData() {
        return data;
    }

    public void setData(PunchlistEmailData data) {
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

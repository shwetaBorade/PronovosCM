package com.pronovoscm.model.response.forgetpassword;

import com.google.gson.annotations.SerializedName;

public class ForgetPasswordResponse {

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private ForgetPasswordData data;
    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ForgetPasswordData getData() {
        return data;
    }

    public void setData(ForgetPasswordData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

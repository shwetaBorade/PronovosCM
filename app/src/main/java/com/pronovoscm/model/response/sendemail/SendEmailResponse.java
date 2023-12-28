package com.pronovoscm.model.response.sendemail;

import com.google.gson.annotations.SerializedName;

public class SendEmailResponse {

    @SerializedName("data")
    private SendEmailResData mSendEmailResData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public SendEmailResData getSendEmailResData() {
        return mSendEmailResData;
    }

    public void setSendEmailResData(SendEmailResData sendEmailResData) {
        mSendEmailResData = sendEmailResData;
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


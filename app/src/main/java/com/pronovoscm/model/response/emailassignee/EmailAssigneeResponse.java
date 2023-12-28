package com.pronovoscm.model.response.emailassignee;

import com.google.gson.annotations.SerializedName;

public class EmailAssigneeResponse {

    @SerializedName("data")
    private EmailAssigneeData mEmailAssigneeData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public EmailAssigneeData getEmailAssigneeData() {
        return mEmailAssigneeData;
    }

    public void setEmailAssigneeData(EmailAssigneeData emailAssigneeData) {
        mEmailAssigneeData = emailAssigneeData;
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

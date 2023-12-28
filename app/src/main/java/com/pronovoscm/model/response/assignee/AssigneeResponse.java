package com.pronovoscm.model.response.assignee;

import com.google.gson.annotations.SerializedName;

public class AssigneeResponse {

    @SerializedName("data")
    private AssigneeData mAssigneeData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public AssigneeData getData() {
        return mAssigneeData;
    }

    public void setData(AssigneeData data) {
        this.mAssigneeData = data;
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

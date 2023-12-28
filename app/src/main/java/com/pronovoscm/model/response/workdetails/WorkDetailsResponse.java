package com.pronovoscm.model.response.workdetails;

import com.google.gson.annotations.SerializedName;

public class WorkDetailsResponse {

    @SerializedName("data")
    private WorkDetailsData mWorkDetailsData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public WorkDetailsData getWorkDetailsData() {
        return mWorkDetailsData;
    }

    public void setWorkDetailsData(WorkDetailsData workDetailsData) {
        mWorkDetailsData = workDetailsData;
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

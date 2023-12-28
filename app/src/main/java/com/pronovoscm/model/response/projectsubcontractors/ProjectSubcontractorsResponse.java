package com.pronovoscm.model.response.projectsubcontractors;

import com.google.gson.annotations.SerializedName;

public  class ProjectSubcontractorsResponse {
    @SerializedName("data")
    private SubcontractorData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public SubcontractorData getData() {
        return data;
    }

    public void setData(SubcontractorData data) {
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

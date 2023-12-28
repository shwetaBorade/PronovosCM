package com.pronovoscm.model.response.forms;

import com.google.gson.annotations.SerializedName;

public  class ProjectFormResponse {

    @SerializedName("data")
    private ProjectFormData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public ProjectFormData getData() {
        return data;
    }

    public void setData(ProjectFormData data) {
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

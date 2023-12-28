package com.pronovoscm.model.response.resources;

import com.google.gson.annotations.SerializedName;

public  class ProjectResourcesResponse {
    @SerializedName("data")
    private ResourceData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public ResourceData getData() {
        return data;
    }

    public void setData(ResourceData data) {
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

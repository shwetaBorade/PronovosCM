package com.pronovoscm.model.response.rfi.replies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiRepliesResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private RfiRepliesData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RfiRepliesData getData() {
        return data;
    }

    public void setData(RfiRepliesData data) {
        this.data = data;
    }
}

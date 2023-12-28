package com.pronovoscm.model.response.rfi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private RfiResponseListData data;

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

    public RfiResponseListData getData() {
        return data;
    }

    public void setData(RfiResponseListData data) {
        this.data = data;
    }

}

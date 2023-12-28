package com.pronovoscm.model.response.rfi.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiContactListResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private RfiContactListResponseData data;

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

    public RfiContactListResponseData getData() {
        return data;
    }

    public void setData(RfiContactListResponseData data) {
        this.data = data;
    }
}

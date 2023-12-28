package com.pronovoscm.model.response.rfi.attachment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiAttachmentResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private RfiAttachmentResponseData data;

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

    public RfiAttachmentResponseData getData() {
        return data;
    }

    public void setData(RfiAttachmentResponseData data) {
        this.data = data;
    }

}

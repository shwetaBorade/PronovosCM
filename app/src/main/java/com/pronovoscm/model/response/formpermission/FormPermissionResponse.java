package com.pronovoscm.model.response.formpermission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormPermissionResponse {

    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private FormPermissionResponseData data;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FormPermissionResponseData getData() {
        return data;
    }

    public void setData(FormPermissionResponseData data) {
        this.data = data;
    }

}

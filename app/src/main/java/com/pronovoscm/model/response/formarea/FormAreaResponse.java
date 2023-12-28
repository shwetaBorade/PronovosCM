package com.pronovoscm.model.response.formarea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormAreaResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private FormAreaResponseData data;

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

    public FormAreaResponseData getData() {
        return data;
    }

    public void setData(FormAreaResponseData data) {
        this.data = data;
    }

}

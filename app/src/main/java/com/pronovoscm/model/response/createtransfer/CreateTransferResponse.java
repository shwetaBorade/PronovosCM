package com.pronovoscm.model.response.createtransfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class CreateTransferResponse {
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

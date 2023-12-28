package com.pronovoscm.model.response.transferdelete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class TransferDeleteResponse {


    @Expose
    @SerializedName("status")
    private int status;
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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
}

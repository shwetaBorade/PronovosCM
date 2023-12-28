package com.pronovoscm.model.response.drawinglist;

import com.google.gson.annotations.SerializedName;

public class DrawingListResponse {


    @SerializedName("data")
    private DrawingListData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public DrawingListData getData() {
        return data;
    }

    public void setData(DrawingListData data) {
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

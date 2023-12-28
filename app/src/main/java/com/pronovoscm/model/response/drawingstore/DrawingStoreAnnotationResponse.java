package com.pronovoscm.model.response.drawingstore;

import com.google.gson.annotations.SerializedName;

public class DrawingStoreAnnotationResponse {

    @SerializedName("data")
    private StoreAnnotionData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public StoreAnnotionData getData() {
        return data;
    }

    public void setData(StoreAnnotionData data) {
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

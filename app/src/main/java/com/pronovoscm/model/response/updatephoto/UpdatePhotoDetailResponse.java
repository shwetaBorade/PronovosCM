package com.pronovoscm.model.response.updatephoto;

import com.google.gson.annotations.SerializedName;

public class UpdatePhotoDetailResponse {

    @SerializedName("data")
    private UpdatePhotoDetailData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public UpdatePhotoDetailData getData() {
        return data;
    }

    public void setData(UpdatePhotoDetailData data) {
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

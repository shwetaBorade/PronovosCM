package com.pronovoscm.model.response.formscheduleresponse;

import com.google.gson.annotations.SerializedName;

public class FormScheduleResponse {
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

    @Override
    public String toString() {
        return "FormScheduleResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

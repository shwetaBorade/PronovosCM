package com.pronovoscm.model.response.drawingannotation;

import com.google.gson.annotations.SerializedName;

public class DrawingAnnotationResponse {


    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private AnnotationData mAnnotationData;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AnnotationData getAnnotationData() {
        return mAnnotationData;
    }

    public void setAnnotationData(AnnotationData annotationData) {
        this.mAnnotationData = annotationData;
    }
}

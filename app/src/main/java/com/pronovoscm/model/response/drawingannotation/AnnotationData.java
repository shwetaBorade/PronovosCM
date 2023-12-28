package com.pronovoscm.model.response.drawingannotation;

import com.google.gson.annotations.SerializedName;

public class AnnotationData {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("original_drw_id")
    private int originalDrwId;
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("annotxml")
    private String annotxml;
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getOriginalDrwId() {
        return originalDrwId;
    }

    public void setOriginalDrwId(int originalDrwId) {
        this.originalDrwId = originalDrwId;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getAnnotxml() {
        return annotxml;
    }

    public void setAnnotxml(String annotxml) {
        this.annotxml = annotxml;
    }

}

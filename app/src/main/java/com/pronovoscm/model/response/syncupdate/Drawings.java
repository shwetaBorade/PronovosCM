package com.pronovoscm.model.response.syncupdate;

import com.google.gson.annotations.SerializedName;

public class Drawings {

    @SerializedName("annotation_xml")
    private String annotation;
    @SerializedName("drawing_id")
    private int drawingId;
    @SerializedName("revision_num")
    private int revisionNumber;
    @SerializedName("drawing_name")
    private String drawingName;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("current_revision")
    private int currentRevision;
    @SerializedName("original_drw_id")
    private int originalDrwId;

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int getDrawingId() {
        return drawingId;
    }

    public void setDrawingId(int drawingId) {
        this.drawingId = drawingId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public int getOriginalDrwId() {
        return originalDrwId;
    }

    public void setOriginalDrwId(int originalDrwId) {
        this.originalDrwId = originalDrwId;
    }

    public int getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(int currentRevision) {
        this.currentRevision = currentRevision;
    }

    @Override
    public String toString() {
        return "Drawings{" +
                "drawingId=" + drawingId +
                ", drawingName='" + drawingName + '\'' +
                ", currentRevision=" + currentRevision +
                '}';
    }
}

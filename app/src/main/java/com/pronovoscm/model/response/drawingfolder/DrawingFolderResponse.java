package com.pronovoscm.model.response.drawingfolder;

import com.google.gson.annotations.SerializedName;

public class DrawingFolderResponse {

    @SerializedName("data")
    private DrawingFolderData mDrawingFolderData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public DrawingFolderData getDrawingFolderData() {
        return mDrawingFolderData;
    }

    public void setDrawingFolderData(DrawingFolderData drawingFolderData) {
        this.mDrawingFolderData = drawingFolderData;
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

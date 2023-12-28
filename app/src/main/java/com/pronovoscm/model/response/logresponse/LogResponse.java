package com.pronovoscm.model.response.logresponse;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.drawingfolder.DrawingFolderData;

public class LogResponse {

    @SerializedName("data")
    private LogData mDrawingLogs;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public LogData getmDrawingLogs() {
        return mDrawingLogs;
    }

    public void setmDrawingLogs(LogData mDrawingLogs) {
        this.mDrawingLogs = mDrawingLogs;
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

package com.pronovoscm.model.response.logresponse;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.drawingfolder.Drawingfolders;

import java.util.List;

public class LogData {
    @SerializedName("drawing_log")
    private DrawingLogs drawingLog;

    @SerializedName("responseCode")
    private int responseCode;

    @SerializedName("responseMsg")
    private String responseMsg;

    public DrawingLogs getDrawingLog() {
        return drawingLog;
    }

    public void setDrawingLog(DrawingLogs drawingLog) {
        this.drawingLog = drawingLog;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}


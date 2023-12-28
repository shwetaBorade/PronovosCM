package com.pronovoscm.model.response.drawingfolder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrawingFolderData {
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("drawingfolders")
    private List<Drawingfolders> drawingfolders;
    @SerializedName("pj_projects_id")
    private int pj_projects_id;

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

    public List<Drawingfolders> getDrawingfolders() {
        return drawingfolders;
    }

    public void setDrawingfolders(List<Drawingfolders> drawingfolders) {
        this.drawingfolders = drawingfolders;
    }

    public int getPj_projects_id() {
        return pj_projects_id;
    }

    public void setPj_projects_id(int pj_projects_id) {
        this.pj_projects_id = pj_projects_id;
    }
}

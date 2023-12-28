package com.pronovoscm.model.request.drawingfolder;

import com.google.gson.annotations.SerializedName;

public class DrawingFolderRequest {

    @SerializedName("project_id")
    private int project_id;

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }
}

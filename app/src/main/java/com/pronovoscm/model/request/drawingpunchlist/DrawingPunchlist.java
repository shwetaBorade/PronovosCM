package com.pronovoscm.model.request.drawingpunchlist;

import com.google.gson.annotations.SerializedName;

public class DrawingPunchlist {

    @SerializedName("project_id")
    private int projectId;

    public DrawingPunchlist(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

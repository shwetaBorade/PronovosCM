package com.pronovoscm.model.request.projectoverview;

import com.google.gson.annotations.SerializedName;

public class ProjectOverviewRequest {
    @SerializedName("project_id")
    private int projectId;

    public ProjectOverviewRequest(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

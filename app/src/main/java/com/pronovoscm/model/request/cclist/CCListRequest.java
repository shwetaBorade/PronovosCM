package com.pronovoscm.model.request.cclist;

import com.google.gson.annotations.SerializedName;

public class CCListRequest {

    @SerializedName("project_id")
    private int projectId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

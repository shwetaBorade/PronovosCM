package com.pronovoscm.model.request.assignee;

import com.google.gson.annotations.SerializedName;

public class AssigneeRequest {

    @SerializedName("project_id")
    private int projectId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

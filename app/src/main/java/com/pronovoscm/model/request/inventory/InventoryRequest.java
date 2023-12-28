package com.pronovoscm.model.request.inventory;

import com.google.gson.annotations.SerializedName;

public class InventoryRequest {

    @SerializedName("project_id")
    private int projectId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

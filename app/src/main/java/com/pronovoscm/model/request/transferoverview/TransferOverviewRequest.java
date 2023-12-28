package com.pronovoscm.model.request.transferoverview;

import com.google.gson.annotations.SerializedName;

public class TransferOverviewRequest {

    @SerializedName("project_id")
    private int projectId;
    @SerializedName("status")
    private Integer status;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

package com.pronovoscm.model.response.companylist;

import com.google.gson.annotations.SerializedName;

public class CompanyListRequest {

    @SerializedName("project_id")
    private int projectId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

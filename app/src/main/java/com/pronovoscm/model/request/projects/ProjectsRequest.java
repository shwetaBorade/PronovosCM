package com.pronovoscm.model.request.projects;

import com.google.gson.annotations.SerializedName;

public class ProjectsRequest {
    @SerializedName("region_id")
    String regionId;
    ProjectVersionsCheck projectVersionsCheck;

    public ProjectsRequest(String regionId, ProjectVersionsCheck projectVersionsCheck) {
        this.regionId = regionId;
        this.projectVersionsCheck = projectVersionsCheck;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public ProjectVersionsCheck getProjectVersionsCheck() {
        return projectVersionsCheck;
    }

    public void setProjectVersionsCheck(ProjectVersionsCheck projectVersionsCheck) {
        this.projectVersionsCheck = projectVersionsCheck;
    }
}

package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Projects {
    public List<com.pronovoscm.model.response.projects.Projects> getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(List<com.pronovoscm.model.response.projects.Projects> projectsList) {
        this.projectsList = projectsList;
    }

    @SerializedName("region_id")
    private int region_id;
    @SerializedName("projects")
    private List<com.pronovoscm.model.response.projects.Projects> projectsList;

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }
}

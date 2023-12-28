package com.pronovoscm.model.request.rfi;

import com.google.gson.annotations.SerializedName;

public class RfiListRequest {
    @SerializedName("project_id")
    private int project_id;

    public int getProjectId() {
        return project_id;
    }

    public void setProjectId(int project_id) {
        this.project_id = project_id;
    }
}

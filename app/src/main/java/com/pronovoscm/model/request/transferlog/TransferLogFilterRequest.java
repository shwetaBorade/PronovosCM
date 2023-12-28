package com.pronovoscm.model.request.transferlog;

import com.google.gson.annotations.SerializedName;

public  class TransferLogFilterRequest {

    @SerializedName("project_id")
    private int projectId;


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


}

package com.pronovoscm.model.request.punchlisemail;

import com.google.gson.annotations.SerializedName;

public class PunchListEmailRequest {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("punch_list_id")
    private int punchListId;
    @SerializedName("project_id")
    private int projectId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPunchListId() {
        return punchListId;
    }

    public void setPunchListId(int punchListId) {
        this.punchListId = punchListId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

package com.pronovoscm.model.request.formuser;

import com.google.gson.annotations.SerializedName;

public class ProjectFormUserRequest {
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("form_id")
    private Integer formId;

    public ProjectFormUserRequest(int projectId, int formId) {
        this.projectId = projectId;
        this.formId = formId;

    }

    public ProjectFormUserRequest(int projectId) {
        this.projectId = projectId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

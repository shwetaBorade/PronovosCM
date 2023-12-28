package com.pronovoscm.model.request.formcomponent;

import com.google.gson.annotations.SerializedName;

public class ProjectFormComponentRequest {
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("revision_number")
    private int revisionNumber;
    @SerializedName("original_form_id")
    private int originalFormsId;

    public ProjectFormComponentRequest(int originalFormsId, int projectId, int revisionNumber) {
        this.originalFormsId = originalFormsId;
        this.revisionNumber = revisionNumber;
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public int getOriginalFormsId() {
        return originalFormsId;
    }

    public void setOriginalFormsId(int originalFormsId) {
        this.originalFormsId = originalFormsId;
    }
}

package com.pronovoscm.model.request.emaildefaultsettings;

import com.google.gson.annotations.SerializedName;

public class DefaultSettingsRequest {

    @SerializedName("project_id")
    private int projectId;
    @SerializedName("form_id")
    private int formId;

    public DefaultSettingsRequest() {

    }

    public DefaultSettingsRequest(int projectId, int formId) {
        this.formId = formId;
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

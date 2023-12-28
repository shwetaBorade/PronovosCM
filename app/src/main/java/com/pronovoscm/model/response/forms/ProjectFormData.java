package com.pronovoscm.model.response.forms;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectFormData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("project_forms")
    private List<ProjectForms> projectForms;

    public String getResponsemsg() {
        return responsemsg;
    }

    public void setResponsemsg(String responsemsg) {
        this.responsemsg = responsemsg;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public List<ProjectForms> getProjectForms() {
        return projectForms;
    }

    public void setProjectForms(List<ProjectForms> projectForms) {
        this.projectForms = projectForms;
    }
}

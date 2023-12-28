package com.pronovoscm.model.response.formarea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormAreaResponseData {
    @SerializedName("project_areas")
    @Expose
    private ProjectAreas projectAreas;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public ProjectAreas getProjectAreas() {
        return projectAreas;
    }

    public void setProjectAreas(ProjectAreas projectAreas) {
        this.projectAreas = projectAreas;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}

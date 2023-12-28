package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectOverviewInfoResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ProjectOverviewInfoData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectOverviewInfoData getProjectOverviewInfoData() {
        return data;
    }

    public void setProjectOverviewInfoData(ProjectOverviewInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProjectOverviewInfoResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

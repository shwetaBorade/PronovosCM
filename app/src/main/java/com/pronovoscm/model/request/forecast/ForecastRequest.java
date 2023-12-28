package com.pronovoscm.model.request.forecast;

import com.google.gson.annotations.SerializedName;

public class ForecastRequest {


    @SerializedName("project_id")
    private int projectId;
    @SerializedName("report_date")
    private String reportDate;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

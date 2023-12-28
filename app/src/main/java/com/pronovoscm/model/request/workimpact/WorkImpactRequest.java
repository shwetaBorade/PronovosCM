package com.pronovoscm.model.request.workimpact;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkImpactRequest {


    @SerializedName("work_impacts_report")
    private List<WorkImpactsReportRequest> workImpactsReport;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("report_date")
    private String reportDate;

    public List<WorkImpactsReportRequest> getWorkImpactsReport() {
        return workImpactsReport;
    }

    public void setWorkImpactsReport(List<WorkImpactsReportRequest> workImpactsReport) {
        this.workImpactsReport = workImpactsReport;
    }

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

package com.pronovoscm.model.request.workdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkDetailsRequest {


    @SerializedName("work_details_report")
    private List<WorkDetailsReportRequest> workDetailsReport;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("report_date")
    private String reportDate;

    public List<WorkDetailsReportRequest> getWorkDetailsReport() {
        return workDetailsReport;
    }

    public void setWorkDetailsReport(List<WorkDetailsReportRequest> workDetailsReport) {
        this.workDetailsReport = workDetailsReport;
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

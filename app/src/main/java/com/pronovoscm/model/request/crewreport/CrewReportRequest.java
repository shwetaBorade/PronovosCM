package com.pronovoscm.model.request.crewreport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CrewReportRequest {

    @SerializedName("crew_report")
    private List<CrewReport> crewReport;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("report_date")
    private String reportDate;

    public List<CrewReport> getCrewReport() {
        return crewReport;
    }

    public void setCrewReport(List<CrewReport> crewReport) {
        this.crewReport = crewReport;
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

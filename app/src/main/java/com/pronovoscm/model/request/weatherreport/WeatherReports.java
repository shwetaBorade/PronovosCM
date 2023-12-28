package com.pronovoscm.model.request.weatherreport;

import com.google.gson.annotations.SerializedName;

public class WeatherReports {
    @SerializedName("notes")
    private String notes;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("conditions")
    private String conditions;
    @SerializedName("impact")
    private String impact;
    @SerializedName("report_date")
    private String reportDate;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

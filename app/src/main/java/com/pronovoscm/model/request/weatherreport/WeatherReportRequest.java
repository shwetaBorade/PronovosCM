package com.pronovoscm.model.request.weatherreport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherReportRequest {

    @SerializedName("weather_reports")
    private List<WeatherReports> weatherReports;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("report_date")
    private String reportDate;

    public List<WeatherReports> getWeatherReports() {
        return weatherReports;
    }

    public void setWeatherReports(List<WeatherReports> weatherReports) {
        this.weatherReports = weatherReports;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

package com.pronovoscm.model.response.weatherreport;

import com.google.gson.annotations.SerializedName;

public class WeatherReports {
    @SerializedName("daily_report_weather_id_mobile")
    private int dailyReportWeatherIdMobile;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("report_date")
    private String reportDate;
    @SerializedName("is_sent")
    private int isSent;
    @SerializedName("notes")
    private String notes;
    @SerializedName("conditions")
    private String conditions;
    @SerializedName("impact")
    private String impact;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("daily_report_weather_id")
    private int dailyReportWeatherId;

    public int getDailyReportWeatherIdMobile() {
        return dailyReportWeatherIdMobile;
    }

    public void setDailyReportWeatherIdMobile(int dailyReportWeatherIdMobile) {
        this.dailyReportWeatherIdMobile = dailyReportWeatherIdMobile;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public int getIsSent() {
        return isSent;
    }

    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getDailyReportWeatherId() {
        return dailyReportWeatherId;
    }

    public void setDailyReportWeatherId(int dailyReportWeatherId) {
        this.dailyReportWeatherId = dailyReportWeatherId;
    }
}

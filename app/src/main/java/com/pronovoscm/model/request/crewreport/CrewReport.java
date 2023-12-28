package com.pronovoscm.model.request.crewreport;

import com.google.gson.annotations.SerializedName;

public class CrewReport {
    @SerializedName("trade")
    private String trade;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("trades_id")
    private String tradesId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("type")
    private String type;
    @SerializedName("journeyman")
    private String journeyman;
    @SerializedName("foreman")
    private String foreman;
    @SerializedName("supt")
    private String supt;
    @SerializedName("crew_report_id")
    private String crewReportId;
    @SerializedName("apprentice")
    private String apprentice;
    @SerializedName("crew_report_id_mobile")
    private String crewReportIdMobile;
    @SerializedName("company_id")
    private String companyId;

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJourneyman() {
        return journeyman;
    }

    public void setJourneyman(String journeyman) {
        this.journeyman = journeyman;
    }

    public String getForeman() {
        return foreman;
    }

    public void setForeman(String foreman) {
        this.foreman = foreman;
    }

    public String getSupt() {
        return supt;
    }

    public void setSupt(String supt) {
        this.supt = supt;
    }

    public String getCrewReportId() {
        return crewReportId;
    }

    public void setCrewReportId(String crewReportId) {
        this.crewReportId = crewReportId;
    }

    public String getApprentice() {
        return apprentice;
    }

    public void setApprentice(String apprentice) {
        this.apprentice = apprentice;
    }

    public String getCrewReportIdMobile() {
        return crewReportIdMobile;
    }

    public void setCrewReportIdMobile(String crewReportIdMobile) {
        this.crewReportIdMobile = crewReportIdMobile;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

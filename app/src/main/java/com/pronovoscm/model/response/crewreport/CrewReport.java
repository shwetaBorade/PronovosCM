package com.pronovoscm.model.response.crewreport;

import com.google.gson.annotations.SerializedName;

public class CrewReport {
    @SerializedName("apprentice")
    private int apprentice;
    @SerializedName("journeyman")
    private int journeyman;
    @SerializedName("foreman")
    private int foreman;
    @SerializedName("supt")
    private int supt;
    @SerializedName("trades_id")
    private int tradesId;
    @SerializedName("trade")
    private String trade;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("type")
    private String type;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("crew_report_id_mobile")
    private int crewReportIdMobile;
    @SerializedName("company_id")
    private int companyId;
    @SerializedName("crew_report_id")
    private int crewReportId;

    public int getApprentice() {
        return apprentice;
    }

    public void setApprentice(int apprentice) {
        this.apprentice = apprentice;
    }

    public int getJourneyman() {
        return journeyman;
    }

    public void setJourneyman(int journeyman) {
        this.journeyman = journeyman;
    }

    public int getForeman() {
        return foreman;
    }

    public void setForeman(int foreman) {
        this.foreman = foreman;
    }

    public int getSupt() {
        return supt;
    }

    public void setSupt(int supt) {
        this.supt = supt;
    }

    public int getTradesId() {
        return tradesId;
    }

    public void setTradesId(int tradesId) {
        this.tradesId = tradesId;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCrewReportIdMobile() {
        return crewReportIdMobile;
    }

    public void setCrewReportIdMobile(int crewReportIdMobile) {
        this.crewReportIdMobile = crewReportIdMobile;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCrewReportId() {
        return crewReportId;
    }

    public void setCrewReportId(int crewReportId) {
        this.crewReportId = crewReportId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

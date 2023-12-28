package com.pronovoscm.model.response.crewreport;

import com.google.gson.annotations.SerializedName;

public class SyncData {
    @SerializedName("crew_report_id_mobile")
    private String crewReportIdMobile;
    @SerializedName("crew_report_id")
    private String crewReportId;
    @SerializedName("sync")
    private boolean sync;
    @SerializedName("report_date")
    private String reportDate;

    public String getCrewReportIdMobile() {
        return crewReportIdMobile;
    }

    public void setCrewReportIdMobile(String crewReportIdMobile) {
        this.crewReportIdMobile = crewReportIdMobile;
    }

    public boolean getSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getCrewReportId() {
        return crewReportId;
    }

    public void setCrewReportId(String crewReportId) {
        this.crewReportId = crewReportId;
    }

    public boolean isSync() {
        return sync;
    }
}

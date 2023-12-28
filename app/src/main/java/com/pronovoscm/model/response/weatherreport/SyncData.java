package com.pronovoscm.model.response.weatherreport;

import com.google.gson.annotations.SerializedName;

public class SyncData {
    @SerializedName("sync")
    private boolean sync;
    @SerializedName("report_date")
    private String reportDate;

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
}

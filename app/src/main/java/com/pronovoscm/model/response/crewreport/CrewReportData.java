package com.pronovoscm.model.response.crewreport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CrewReportData {
    @SerializedName("sync_data")
    private List<SyncData> syncData;
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("crew_report")
    private List<CrewReport> crewReport;

    public List<SyncData> getSyncData() {
        return syncData;
    }

    public void setSyncData(List<SyncData> syncData) {
        this.syncData = syncData;
    }

    public String getResponsemsg() {
        return responsemsg;
    }

    public void setResponsemsg(String responsemsg) {
        this.responsemsg = responsemsg;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public List<CrewReport> getCrewReport() {
        return crewReport;
    }

    public void setCrewReport(List<CrewReport> crewReport) {
        this.crewReport = crewReport;
    }
}

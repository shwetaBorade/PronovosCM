package com.pronovoscm.model.response.crewreport;

import com.google.gson.annotations.SerializedName;

public class CrewReportResponse {

    @SerializedName("data")
    private CrewReportData mCrewReportData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public CrewReportData getCrewReportData() {
        return mCrewReportData;
    }

    public void setCrewReportData(CrewReportData crewReportData) {
        mCrewReportData = crewReportData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

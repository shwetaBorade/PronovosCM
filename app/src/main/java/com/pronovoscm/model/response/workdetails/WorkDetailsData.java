package com.pronovoscm.model.response.workdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkDetailsData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("work_details_report")
    private List<WorkDetailsReport> workDetailsReport;

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

    public List<WorkDetailsReport> getWorkDetailsReport() {
        return workDetailsReport;
    }

    public void setWorkDetailsReport(List<WorkDetailsReport> workDetailsReport) {
        this.workDetailsReport = workDetailsReport;
    }
}

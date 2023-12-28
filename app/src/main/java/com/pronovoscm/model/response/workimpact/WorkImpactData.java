package com.pronovoscm.model.response.workimpact;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkImpactData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("work_impacts_report")
    private List<WorkImpactsReport> workImpactsReport;

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

    public List<WorkImpactsReport> getWorkImpactsReport() {
        return workImpactsReport;
    }

    public void setWorkImpactsReport(List<WorkImpactsReport> workImpactsReport) {
        this.workImpactsReport = workImpactsReport;
    }
}

package com.pronovoscm.model.response.formscheduleresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("scheduled_forms")
    private List<ScheduledForms> scheduledForms;

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

    public List<ScheduledForms> getScheduledForms() {
        return scheduledForms;
    }

    public void setScheduledForms(List<ScheduledForms> scheduledForms) {
        this.scheduledForms = scheduledForms;
    }

    @Override
    public String toString() {
        return "\n Data{" +
                "responsemsg='" + responsemsg + '\'' +
                ", responsecode=" + responsecode +
                ", scheduledForms=" + scheduledForms +
                '}';
    }
}

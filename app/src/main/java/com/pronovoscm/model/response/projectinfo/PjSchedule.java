package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.SerializedName;

public class PjSchedule {
    @SerializedName("total_duration")
    private double totalDuration;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("start_date")
    private String startDate;

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}

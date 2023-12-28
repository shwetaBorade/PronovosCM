package com.pronovoscm.model.response.darksky;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Daily {

    @SerializedName("summary")
    private String summary;

    @SerializedName("data")
    private List<DailyReport> dailyReportList;

    @SerializedName("icon")
    private String icon;

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public List<DailyReport> getDailyReportList() {
        return dailyReportList;
    }

    public void setDailyReportList(List<DailyReport> dailyReportList) {
        this.dailyReportList = dailyReportList;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Daily{" +
                "summary = '" + summary + '\'' +
                ",data = '" + dailyReportList + '\'' +
                ",icon = '" + icon + '\'' +
                "}";
    }
}
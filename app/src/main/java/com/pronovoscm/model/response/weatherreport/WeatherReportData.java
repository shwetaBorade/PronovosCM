package com.pronovoscm.model.response.weatherreport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherReportData {
    @SerializedName("sync_data")
    private List<SyncData> syncData;
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("weather_reports")
    private WeatherReports weatherReports;


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

    public WeatherReports getWeatherReports() {
        return weatherReports;
    }

    public void setWeatherReports(WeatherReports weatherReports) {
        this.weatherReports = weatherReports;
    }

    public List<SyncData> getSyncData() {
        return syncData;
    }

    public void setSyncData(List<SyncData> syncData) {
        this.syncData = syncData;
    }

}

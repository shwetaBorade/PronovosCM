package com.pronovoscm.model.response.weatherreport;

import com.google.gson.annotations.SerializedName;

public class WeatherReportResponse {

    @SerializedName("data")
    private WeatherReportData mWeatherReportData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public WeatherReportData getWeatherReportData() {
        return mWeatherReportData;
    }

    public void setWeatherReportData(WeatherReportData weatherReportData) {
        mWeatherReportData = weatherReportData;
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

package com.pronovoscm.model.response.weatherconditions;

import com.google.gson.annotations.SerializedName;

public class WeatherConditionsResponse {


    @SerializedName("data")
    private WeatherConditionData mWeatherConditionData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public WeatherConditionData getData() {
        return mWeatherConditionData;
    }

    public void setData(WeatherConditionData data) {
        this.mWeatherConditionData = data;
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

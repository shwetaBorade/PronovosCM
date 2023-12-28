package com.pronovoscm.model.response.forecastresponse;

import com.google.gson.annotations.SerializedName;

public class ForecastResponse {

    @SerializedName("data")
    private ForecastData mForecastData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public ForecastData getForecastData() {
        return mForecastData;
    }

    public void setForecastData(ForecastData forecastData) {
        mForecastData = forecastData;
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

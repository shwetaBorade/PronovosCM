package com.pronovoscm.model.response.weatherconditions;

import com.google.gson.annotations.SerializedName;

public class Conditions {
    @SerializedName("label")
    private String label;
    @SerializedName("weather_conditions_id")
    private int weatherConditionsId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getWeatherConditionsId() {
        return weatherConditionsId;
    }

    public void setWeatherConditionsId(int weatherConditionsId) {
        this.weatherConditionsId = weatherConditionsId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

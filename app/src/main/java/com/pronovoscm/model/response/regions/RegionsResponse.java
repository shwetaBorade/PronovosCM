package com.pronovoscm.model.response.regions;

import com.google.gson.annotations.SerializedName;

public class RegionsResponse {

    @SerializedName("data")
    private RegionData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public RegionData getRegionData() {
        return data;
    }

    public void setRegionData(RegionData regionData) {
        this.data = regionData;
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

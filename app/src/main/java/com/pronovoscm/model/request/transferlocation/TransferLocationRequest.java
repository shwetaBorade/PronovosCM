package com.pronovoscm.model.request.transferlocation;

import com.google.gson.annotations.SerializedName;

public class TransferLocationRequest {

    @SerializedName("region_id")
    private int regionId;

    @SerializedName("type")
    private String type;

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

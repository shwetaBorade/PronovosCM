package com.pronovoscm.model.request.transfercontact;

import com.google.gson.annotations.SerializedName;

public class TransferContactRequest {

    @SerializedName("region_id")
    private int regionId;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("location_id")
    private int locationId;
    @SerializedName("vendor_id")
    private int vendorId;
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

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }
}

package com.pronovoscm.model.request.transferlog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class TransferLogRequest {

    @SerializedName("page")
    private int page;
    @SerializedName("dropoff_date_to")
    private String dropoffDateTo;
    @SerializedName("dropoff_date_from")
    private String dropoffDateFrom;
    @SerializedName("pickup_date_to")
    private String pickupDateTo;
    @SerializedName("pickup_date_from")
    private String pickupDateFrom;
    @SerializedName("dropoff_location")
    private int dropoffLocation;
    @SerializedName("pickup_location")
    private int pickupLocation;
    @SerializedName("project_id")
    private int projectId;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getDropoffDateTo() {
        return dropoffDateTo;
    }

    public void setDropoffDateTo(String dropoffDateTo) {
        this.dropoffDateTo = dropoffDateTo;
    }

    public String getDropoffDateFrom() {
        return dropoffDateFrom;
    }

    public void setDropoffDateFrom(String dropoffDateFrom) {
        this.dropoffDateFrom = dropoffDateFrom;
    }

    public String getPickupDateTo() {
        return pickupDateTo;
    }

    public void setPickupDateTo(String pickupDateTo) {
        this.pickupDateTo = pickupDateTo;
    }

    public String getPickupDateFrom() {
        return pickupDateFrom;
    }

    public void setPickupDateFrom(String pickupDateFrom) {
        this.pickupDateFrom = pickupDateFrom;
    }

    public int getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(int dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public int getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(int pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


}

package com.pronovoscm.model.response.transferlog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logs {
    @SerializedName("drop_off_date")
    private String dropOffDate;
    @SerializedName("pick_up_date")
    private String pickUpDate;
    @SerializedName("drop_off_location")
    private String dropOffLocation;
    @SerializedName("pick_up_location")
    private String pickUpLocation;
    @SerializedName("transfer_id")
    private int transferId;

    public String getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(String dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}

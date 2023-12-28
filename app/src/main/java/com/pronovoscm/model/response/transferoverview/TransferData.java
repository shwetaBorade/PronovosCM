package com.pronovoscm.model.response.transferoverview;

import com.google.gson.annotations.SerializedName;

public class TransferData {
    @SerializedName("dropoff_location")
    private String dropoffLocation;
    @SerializedName("pickup_location")
    private String pickupLocation;
    @SerializedName("dropoff_time")
    private String dropoffTime;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("pickup_is_vendor")
    private int pickupIsVendor;
    @SerializedName("dropoff_is_vendor")
    private int dropoffIsVendor;
    @SerializedName("shipped_to")
    private int shippedTo;
    @SerializedName("shipped_from")
    private int shippedFrom;
    @SerializedName("delivery_date")
    private String deliveryDate;
    @SerializedName("pickup_date")
    private String pickupDate;
    @SerializedName("status")
    private int status;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("eq_transfer_requests_id")
    private int eqTransferRequestsId;
    @SerializedName("interoffice_transfer")
    private int interofficeTransfer;

    public int getInterofficeTransfer() {
        return interofficeTransfer;
    }

    public void setInterofficeTransfer(int interofficeTransfer) {
        this.interofficeTransfer = interofficeTransfer;
    }

    @SerializedName("transfer_type")
    private int transferType;

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getPickupIsVendor() {
        return pickupIsVendor;
    }

    public void setPickupIsVendor(int pickupIsVendor) {
        this.pickupIsVendor = pickupIsVendor;
    }

    public int getDropoffIsVendor() {
        return dropoffIsVendor;
    }

    public void setDropoffIsVendor(int dropoffIsVendor) {
        this.dropoffIsVendor = dropoffIsVendor;
    }

    public int getShippedTo() {
        return shippedTo;
    }

    public void setShippedTo(int shippedTo) {
        this.shippedTo = shippedTo;
    }

    public int getShippedFrom() {
        return shippedFrom;
    }

    public void setShippedFrom(int shippedFrom) {
        this.shippedFrom = shippedFrom;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getEqTransferRequestsId() {
        return eqTransferRequestsId;
    }

    public void setEqTransferRequestsId(int eqTransferRequestsId) {
        this.eqTransferRequestsId = eqTransferRequestsId;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }
}

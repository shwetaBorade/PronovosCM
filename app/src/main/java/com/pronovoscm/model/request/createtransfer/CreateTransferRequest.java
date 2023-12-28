package com.pronovoscm.model.request.createtransfer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateTransferRequest {
    @SerializedName("equipment")
    private List<CreateTransferEquipment> createTransferEquipments;
    @SerializedName("actual_dropoff_departure_time")
    private String actualDropoffDepartureTime;
    @SerializedName("actual_dropoff_load_time")
    private String actualDropoffLoadTime;
    @SerializedName("actual_dropoff_time")
    private String actualDropoffTime;
    @SerializedName("actual_pickup_departure_time")
    private String actualPickupDepartureTime;
    @SerializedName("actual_pickup_load_time")
    private String actualPickupLoadTime;
    @SerializedName("actual_pickup_time")
    private String actualPickupTime;
    @SerializedName("comments")
    private String comments;
    @SerializedName("dispute_notes")
    private String disputeNotes;
    @SerializedName("freight_line")
    private String freightLine;
    @SerializedName("status")
    private int status;
    @SerializedName("vendor_location")
    private int vendorLocation;
    @SerializedName("pickup_vendor_status")
    private int pickupVendorStatus;
    @SerializedName("dropoff_vendor_status")
    private int dropOffVendorStatus;
    @SerializedName("unloading")
    private String unloading;
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("drop_off_phone")
    private String dropOffPhone;
    @SerializedName("drop_off_name")
    private String dropOffName;
    @SerializedName("pickup_phone")
    private String pickupPhone;
    @SerializedName("pickup_name")
    private String pickupName;
    @SerializedName("drop_off_location")
    private String dropOffLocation;
    @SerializedName("pickup_location")
    private String pickupLocation;
    @SerializedName("dropoff_time")
    private String dropoffTime;
    @SerializedName("delivery_date")
    private String deliveryDate;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("pickup_date")
    private String pickupDate;
    @SerializedName("transfer_id")
    private int transferId;

    public List<CreateTransferEquipment> getCreateTransferEquipments() {
        return createTransferEquipments;
    }

    public void setCreateTransferEquipments(List<CreateTransferEquipment> createTransferEquipments) {
        this.createTransferEquipments = createTransferEquipments;
    }

    public String getActualDropoffDepartureTime() {
        return actualDropoffDepartureTime;
    }

    public void setActualDropoffDepartureTime(String actualDropoffDepartureTime) {
        this.actualDropoffDepartureTime = actualDropoffDepartureTime;
    }

    public String getActualDropoffLoadTime() {
        return actualDropoffLoadTime;
    }

    public void setActualDropoffLoadTime(String actualDropoffLoadTime) {
        this.actualDropoffLoadTime = actualDropoffLoadTime;
    }

    public String getActualDropoffTime() {
        return actualDropoffTime;
    }

    public void setActualDropoffTime(String actualDropoffTime) {
        this.actualDropoffTime = actualDropoffTime;
    }

    public String getActualPickupDepartureTime() {
        return actualPickupDepartureTime;
    }

    public void setActualPickupDepartureTime(String actualPickupDepartureTime) {
        this.actualPickupDepartureTime = actualPickupDepartureTime;
    }

    public String getActualPickupLoadTime() {
        return actualPickupLoadTime;
    }

    public void setActualPickupLoadTime(String actualPickupLoadTime) {
        this.actualPickupLoadTime = actualPickupLoadTime;
    }

    public String getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(String actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFreightLine() {
        return freightLine;
    }

    public void setFreightLine(String freightLine) {
        this.freightLine = freightLine;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(int vendorLocation) {
        this.vendorLocation = vendorLocation;
    }

    public int getPickupVendorStatus() {
        return pickupVendorStatus;
    }

    public void setPickupVendorStatus(int pickupVendorStatus) {
        this.pickupVendorStatus = pickupVendorStatus;
    }

    public int getDropOffVendorStatus() {
        return dropOffVendorStatus;
    }

    public void setDropOffVendorStatus(int dropOffVendorStatus) {
        this.dropOffVendorStatus = dropOffVendorStatus;
    }

    public String getUnloading() {
        return unloading;
    }

    public void setUnloading(String unloading) {
        this.unloading = unloading;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDropOffPhone() {
        return dropOffPhone;
    }

    public void setDropOffPhone(String dropOffPhone) {
        this.dropOffPhone = dropOffPhone;
    }

    public String getDropOffName() {
        return dropOffName;
    }

    public void setDropOffName(String dropOffName) {
        this.dropOffName = dropOffName;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public void setPickupPhone(String pickupPhone) {
        this.pickupPhone = pickupPhone;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
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

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getDisputeNotes() {
        return disputeNotes;
    }

    public void setDisputeNotes(String disputeNotes) {
        this.disputeNotes = disputeNotes;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}

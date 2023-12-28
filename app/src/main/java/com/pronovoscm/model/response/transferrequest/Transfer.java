package com.pronovoscm.model.response.transferrequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transfer {
    @SerializedName("equipment")
    private List<Equipment> equipment;
    @SerializedName("transfer_id")
    private int transferId;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("round_trip")
    private int roundTrip;
    @SerializedName("truck_size")
    private String truckSize;
    @SerializedName("comments")
    private String comments;
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("freight_line")
    private String freightLine;
    @SerializedName("delivery_number")
    private String deliveryNumber;
    @SerializedName("delivery_contact")
    private String deliveryContact;
    @SerializedName("dropoff_is_vendor")
    private int dropoffIsVendor;
    @SerializedName("pickup_is_vendor")
    private int pickupIsVendor;
    @SerializedName("dropoff_time")
    private String dropoffTime;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("pickup_phone")
    private String pickupPhone;
    @SerializedName("pickup_contact")
    private String pickupContact;
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




    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(int roundTrip) {
        this.roundTrip = roundTrip;
    }

    public String getTruckSize() {
        return truckSize;
    }

    public void setTruckSize(String truckSize) {
        this.truckSize = truckSize;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getFreightLine() {
        return freightLine;
    }

    public void setFreightLine(String freightLine) {
        this.freightLine = freightLine;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getDeliveryContact() {
        return deliveryContact;
    }

    public void setDeliveryContact(String deliveryContact) {
        this.deliveryContact = deliveryContact;
    }

    public int getDropoffIsVendor() {
        return dropoffIsVendor;
    }

    public void setDropoffIsVendor(int dropoffIsVendor) {
        this.dropoffIsVendor = dropoffIsVendor;
    }

    public int getPickupIsVendor() {
        return pickupIsVendor;
    }

    public void setPickupIsVendor(int pickupIsVendor) {
        this.pickupIsVendor = pickupIsVendor;
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

    public String getPickupPhone() {
        return pickupPhone;
    }

    public void setPickupPhone(String pickupPhone) {
        this.pickupPhone = pickupPhone;
    }

    public String getPickupContact() {
        return pickupContact;
    }

    public void setPickupContact(String pickupContact) {
        this.pickupContact = pickupContact;
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
}

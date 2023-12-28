package com.pronovoscm.model.request.createtransfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTransferEquipment {
    @SerializedName("transfer_equipment_id")
    private int transferEquipmentId;
    @SerializedName("tracking_number")
    private String trackingNumber;
    @SerializedName("equipment_id")
    private int equipmentId;
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("unit")
    private String unit;
    @SerializedName("weight")
    private String weight;
    @SerializedName("quantity")
    private String quantity;
    @SerializedName("equipment_status")
    private String equipmentStatus;
    @SerializedName("status")
    private String status;
    @SerializedName("name")
    private String name;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(String equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTransferEquipmentId() {
        return transferEquipmentId;
    }

    public void setTransferEquipmentId(int transferEquipmentId) {
        this.transferEquipmentId = transferEquipmentId;
    }
}

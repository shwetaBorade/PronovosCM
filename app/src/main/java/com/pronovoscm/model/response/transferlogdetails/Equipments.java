package com.pronovoscm.model.response.transferlogdetails;

import com.google.gson.annotations.SerializedName;

public class Equipments {
    @SerializedName("equipment_status")
    private int equipmentStatus;
    @SerializedName("tracking")
    private String tracking;
    @SerializedName("total_weight")
    private float totalWeight;
    @SerializedName("weight")
    private String weight;
    @SerializedName("units")
    private String units;
    @SerializedName("equipment_id")
    private int equipmentId;
    @SerializedName("transfer_equipment_id")
    private int transferEquipmentId;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private int status;

    public int getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(int equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getTransferEquipmentId() {
        return transferEquipmentId;
    }

    public void setTransferEquipmentId(int transferEquipmentId) {
        this.transferEquipmentId = transferEquipmentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.pronovoscm.model.response.transferrequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Equipment implements Serializable {

    @SerializedName("equipment_id")
    private int equipmentId;
    @SerializedName("equipment_status")
    private int equipmentStatus;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("total_weight")
    private float totalWeight;
    @SerializedName("unit")
    private String unit;
    @SerializedName("weight")
    private String weight;
    @SerializedName("transfer_id")
    private int transferId;
    @SerializedName("eq_transfer_requests_id")
    private int eqTransferRequestsId;
    @SerializedName("status")
    private int status;
    @SerializedName("name")
    private String name;
    @SerializedName("transfer_equipment_id")
    private int transferEquipmentId;

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(int equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(float totalWeight) {
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

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getEqTransferRequestsId() {
        return eqTransferRequestsId;
    }

    public void setEqTransferRequestsId(int eqTransferRequestsId) {
        this.eqTransferRequestsId = eqTransferRequestsId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

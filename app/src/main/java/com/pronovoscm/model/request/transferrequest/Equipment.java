package com.pronovoscm.model.request.transferrequest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Equipment implements Serializable, Parcelable {

    public static final Creator<Equipment> CREATOR = new Creator<Equipment>() {
        @Override
        public Equipment createFromParcel(Parcel in) {
            return new Equipment(in);
        }

        @Override
        public Equipment[] newArray(int size) {
            return new Equipment[size];
        }
    };
    @SerializedName("transfer_equipment_id")
    private int transferEquipmentId;
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("quantity")
    private String quantity;
    @SerializedName("equipment_id")
    private int equipmentId;
    @SerializedName("unit")
    private float unit;
    @SerializedName("weight")
    private String weight;
    @SerializedName("status")
    private int status;
    @SerializedName("equipment_status")
    private int equipmentStatus;
    @SerializedName("name")
    private String name;
    @SerializedName("tracking_number")
    private String trackingNumber;

    public Equipment() {
        trackingNumber = "";
        transferEquipmentId=0;
    }


    protected Equipment(Parcel in) {
        transferEquipmentId = in.readInt();
        totalWeight = in.readString();
        quantity = in.readString();
        equipmentId = in.readInt();
        unit = in.readFloat();
        weight = in.readString();
        status = in.readInt();
        equipmentStatus = in.readInt();
        name = in.readString();
        trackingNumber = in.readString();
    }

    public int getTransferEquipmentId() {
        return transferEquipmentId;
    }

    public void setTransferEquipmentId(int transferEquipmentId) {
        this.transferEquipmentId = transferEquipmentId;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public float getUnit() {
        return unit;
    }

    public void setUnit(float unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(int equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(transferEquipmentId);
        parcel.writeString(totalWeight);
        parcel.writeString(quantity);
        parcel.writeInt(equipmentId);
        parcel.writeFloat(unit);
        parcel.writeString(weight);
        parcel.writeInt(status);
        parcel.writeInt(equipmentStatus);
        parcel.writeString(name);
        parcel.writeString(trackingNumber);
    }
}

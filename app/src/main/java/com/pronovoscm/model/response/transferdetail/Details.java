package com.pronovoscm.model.response.transferdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Details implements Serializable, Parcelable {
    @SerializedName("equipments")
    private List<Equipments> equipments;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("transfer_id")
    private int transferId;
    @SerializedName("round_trip")
    private int roundTrip;
    @SerializedName("comments")
    private String comments;
    @SerializedName("unloading_method")
    private int unloadingMethod;
    @SerializedName("actual_dropoff_departure_time")
    private String actualDropoffDepartureTime;
    @SerializedName("actual_dropoff_load_time")
    private String actualDropoffLoadTime;
    @SerializedName("actual_dropoff_time")
    private String actualDropoffTime;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("dropoff_contact_number")
    private String dropoffContactNumber;
    @SerializedName("dropoff_contact")
    private String dropoffContact;
    @SerializedName("dropoff_location")
    private int dropoffLocation;
    @SerializedName("dropoff_time")
    private String dropoffTime;
    @SerializedName("dropoff_date")
    private String dropoffDate;
    @SerializedName("actual_pickup_departure_time")
    private String actualPickupDepartureTime;
    @SerializedName("actual_pickup_load_time")
    private String actualPickupLoadTime;
    @SerializedName("actual_pickup_time")
    private String actualPickupTime;
    @SerializedName("freight_line")
    private String freightLine;
    @SerializedName("truck_size")
    private String truckSize;
    @SerializedName("pickup_contact_number")
    private String pickupContactNumber;
    @SerializedName("pickup_contact")
    private String pickupContact;
    @SerializedName("pickup_location")
    private int pickupLocation;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("vendor_location")
    private int vendorLocation;
    @SerializedName("dropoff_is_vendor")
    private int dropoffIsVendor;
    @SerializedName("pickup_is_vendor")
    private int pickupIsVendor;
    @SerializedName("status")
    private int status;
    @SerializedName("pickup_date")
    private String pickupDate;

    @SerializedName("pickup_location_name")
    private String pickupLocationName;
    @SerializedName("dropoff_location_name")
    private String dropoffLocationName;
    @SerializedName("interoffice_transfer")
    private boolean interofficeTransfer;

    protected Details(Parcel in) {
        updatedAt = in.readString();
        createdAt = in.readString();
        transferId = in.readInt();
        roundTrip = in.readInt();
        comments = in.readString();
        unloadingMethod = in.readInt();
        actualDropoffDepartureTime = in.readString();
        actualDropoffLoadTime = in.readString();
        actualDropoffTime = in.readString();
        createdBy = in.readString();
        totalWeight = in.readString();
        dropoffContactNumber = in.readString();
        dropoffContact = in.readString();
        dropoffLocation = in.readInt();
        dropoffTime = in.readString();
        dropoffDate = in.readString();
        actualPickupDepartureTime = in.readString();
        actualPickupLoadTime = in.readString();
        actualPickupTime = in.readString();
        freightLine = in.readString();
        truckSize = in.readString();
        pickupContactNumber = in.readString();
        pickupContact = in.readString();
        pickupLocation = in.readInt();
        pickupTime = in.readString();
        projectId = in.readInt();
        vendorLocation = in.readInt();
        dropoffIsVendor = in.readInt();
        pickupIsVendor = in.readInt();
        status = in.readInt();
        interofficeTransfer = in.readByte() != 0;
        pickupLocationName = in.readString();
        dropoffLocationName = in.readString();
        pickupDate = in.readString();
        this.equipments = new ArrayList<Equipments>();
        in.readTypedList(equipments, Equipments.CREATOR);

    }

    public boolean isInterofficeTransfer() {
        return interofficeTransfer;
    }

    public void setInterofficeTransfer(boolean interofficeTransfer) {
        this.interofficeTransfer = interofficeTransfer;
    }

    public static final Creator<Details> CREATOR = new Creator<Details>() {
        @Override
        public Details createFromParcel(Parcel in) {
            return new Details(in);
        }

        @Override
        public Details[] newArray(int size) {
            return new Details[size];
        }
    };

    public List<Equipments> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipments> equipments) {
        this.equipments = equipments;
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

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(int roundTrip) {
        this.roundTrip = roundTrip;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getUnloadingMethod() {
        return unloadingMethod;
    }

    public void setUnloadingMethod(int unloadingMethod) {
        this.unloadingMethod = unloadingMethod;
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

    public String getPickupLocationName() {
        return pickupLocationName;
    }

    public void setPickupLocationName(String pickupLocationName) {
        this.pickupLocationName = pickupLocationName;
    }

    public String getDropoffLocationName() {
        return dropoffLocationName;
    }

    public void setDropoffLcationName(String dropoffLcationName) {
        this.dropoffLocationName = dropoffLcationName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getDropoffContactNumber() {
        return dropoffContactNumber;
    }

    public void setDropoffContactNumber(String dropoffContactNumber) {
        this.dropoffContactNumber = dropoffContactNumber;
    }

    public String getDropoffContact() {
        return dropoffContact;
    }

    public void setDropoffContact(String dropoffContact) {
        this.dropoffContact = dropoffContact;
    }

    public int getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(int dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getDropoffDate() {
        return dropoffDate;
    }

    public void setDropoffDate(String dropoffDate) {
        this.dropoffDate = dropoffDate;
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

    public String getFreightLine() {
        return freightLine;
    }

    public void setFreightLine(String freightLine) {
        this.freightLine = freightLine;
    }

    public String getTruckSize() {
        return truckSize;
    }

    public void setTruckSize(String truckSize) {
        this.truckSize = truckSize;
    }

    public String getPickupContactNumber() {
        return pickupContactNumber;
    }

    public void setPickupContactNumber(String pickupContactNumber) {
        this.pickupContactNumber = pickupContactNumber;
    }

    public String getPickupContact() {
        return pickupContact;
    }

    public void setPickupContact(String pickupContact) {
        this.pickupContact = pickupContact;
    }

    public int getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(int pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(int vendorLocation) {
        this.vendorLocation = vendorLocation;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(updatedAt);
        parcel.writeString(createdAt);
        parcel.writeInt(transferId);
        parcel.writeInt(roundTrip);
        parcel.writeString(comments);
        parcel.writeInt(unloadingMethod);
        parcel.writeString(actualDropoffDepartureTime);
        parcel.writeString(actualDropoffLoadTime);
        parcel.writeString(actualDropoffTime);
        parcel.writeString(createdBy);
        parcel.writeString(totalWeight);
        parcel.writeString(dropoffContactNumber);
        parcel.writeString(dropoffContact);
        parcel.writeInt(dropoffLocation);
        parcel.writeString(dropoffTime);
        parcel.writeString(dropoffDate);
        parcel.writeString(actualPickupDepartureTime);
        parcel.writeString(actualPickupLoadTime);
        parcel.writeString(actualPickupTime);
        parcel.writeString(freightLine);
        parcel.writeString(truckSize);
        parcel.writeString(pickupContactNumber);
        parcel.writeString(pickupContact);
        parcel.writeInt(pickupLocation);
        parcel.writeString(pickupTime);
        parcel.writeInt(projectId);
        parcel.writeInt(vendorLocation);
        parcel.writeInt(dropoffIsVendor);
        parcel.writeInt(pickupIsVendor);
        parcel.writeInt(status);
        parcel.writeByte((byte) (interofficeTransfer ? 1 : 0));
        parcel.writeString(pickupLocationName);
        parcel.writeString(dropoffLocationName);

        parcel.writeString(pickupDate);
        parcel.writeTypedList(equipments);
    }
}

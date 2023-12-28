package com.pronovoscm.model.request.transferrequest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransferRequest implements Serializable, Parcelable {

    public static final Creator<TransferRequest> CREATOR = new Creator<TransferRequest>() {
        @Override
        public TransferRequest createFromParcel(Parcel in) {
            return new TransferRequest(in);
        }

        @Override
        public TransferRequest[] newArray(int size) {
            return new TransferRequest[size];
        }
    };
    @SerializedName("total_weight")
    private String totalWeight;
    @SerializedName("equipment")
    private List<Equipment> equipment;
    @SerializedName("dropoff_vendor_status")
    private int dropoffVendorStatus;
    @SerializedName("pickup_location")
    private int pickupLocation;
    @SerializedName("delivery_date")
    private String deliveryDate;
    @SerializedName("status")
    private int status;
    @SerializedName("pickup_date")
    private String pickupDate;
    @SerializedName("pickup_vendor_status")
    private int pickupVendorStatus;
    @SerializedName("vendor_location")
    private int vendorLocation;
    @SerializedName("unloading")
    private int unloading;
    @SerializedName("drop_off_location")
    private int dropOffLocation;
    @SerializedName("drop_off_phone")
    private String dropOffPhone;
    @SerializedName("comments")
    private String comments;
    @SerializedName("truck_size")
    private String truckSize;
    @SerializedName("round_trip")
    private int roundTrip;
    @SerializedName("freight_line")
    private String freightLine;
    @SerializedName("pickup_name")
    private String pickupName;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("drop_off_name")
    private String dropOffName;
    @SerializedName("pickup_phone")
    private String pickupPhone;
    @SerializedName("dropoff_time")
    private String dropoffTime;
    @SerializedName("pickup_time")
    private String pickupTime;
    //    private Calendar dropoffCalendar;
//    private Contacts pickUpContacts;
//    private Contacts dropOffContacts;
    @SerializedName("transfer_id")
    private int transferId;
    private String dropoffDepartureTime;
    private String dropoffLoadTime;
    private String dropoffArriveTime;
    private String pickupDepartureTime;
    private String pickupLoadTime;
    private String pickupArriveTime;


    @SerializedName("interoffice_transfer")
    private boolean interofficeTransfer;
    // @SerializedName("pickup_location_name")
    private String pickupLocationName;
    //  @SerializedName("dropoff_location_name")
    private String dropoffLocationName;

    protected TransferRequest(Parcel in) {
        totalWeight = in.readString();
        dropoffVendorStatus = in.readInt();
        pickupLocation = in.readInt();
        deliveryDate = in.readString();
        status = in.readInt();
        pickupDate = in.readString();
        pickupVendorStatus = in.readInt();
        vendorLocation = in.readInt();
        unloading = in.readInt();
        dropOffLocation = in.readInt();
        dropOffPhone = in.readString();
        comments = in.readString();
        truckSize = in.readString();
        roundTrip = in.readInt();
        freightLine = in.readString();
        pickupName = in.readString();
        projectId = in.readInt();
        dropOffName = in.readString();
        pickupPhone = in.readString();
        dropoffTime = in.readString();
        pickupTime = in.readString();
        transferId = in.readInt();
        dropoffArriveTime = in.readString();
        dropoffLoadTime = in.readString();
        dropoffDepartureTime = in.readString();
        pickupArriveTime = in.readString();
        pickupLoadTime = in.readString();
        interofficeTransfer = in.readByte() != 0;
        pickupLocationName = in.readString();
        dropoffLocationName = in.readString();
        pickupDepartureTime = in.readString();
        this.equipment = new ArrayList();
        in.readTypedList(equipment, Equipment.CREATOR);
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

    public void setDropoffLocationName(String dropoffLcationName) {
        this.dropoffLocationName = dropoffLcationName;
    }

    public boolean isInterofficeTransfer() {
        return interofficeTransfer;
    }

    public TransferRequest() {
        pickupVendorStatus = 0;
        dropoffVendorStatus = 0;
        pickupLocation = 0;
        dropOffLocation = 0;
        vendorLocation = 0;
        status = 0;
        transferId = 0;
    }

    public void setInterofficeTransfer(boolean interofficeTransfer) {
        this.interofficeTransfer = interofficeTransfer;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public int getDropoffVendorStatus() {
        return dropoffVendorStatus;
    }

    public void setDropoffVendorStatus(int dropoffVendorStatus) {
        this.dropoffVendorStatus = dropoffVendorStatus;
    }

    public int getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(int pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
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

    public int getPickupVendorStatus() {
        return pickupVendorStatus;
    }

    public void setPickupVendorStatus(int pickupVendorStatus) {
        this.pickupVendorStatus = pickupVendorStatus;
    }

    public int getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(int vendorLocation) {
        this.vendorLocation = vendorLocation;
    }

    public int getUnloading() {
        return unloading;
    }

    public void setUnloading(int unloading) {
        this.unloading = unloading;
    }

    public int getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(int dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getDropOffPhone() {
        return dropOffPhone;
    }

    public void setDropOffPhone(String dropOffPhone) {
        this.dropOffPhone = dropOffPhone;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTruckSize() {
        return truckSize;
    }

    public void setTruckSize(String truckSize) {
        this.truckSize = truckSize;
    }

    public int getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(int roundTrip) {
        this.roundTrip = roundTrip;
    }

    public String getFreightLine() {
        return freightLine;
    }

    public void setFreightLine(String freightLine) {
        this.freightLine = freightLine;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(totalWeight);
        parcel.writeInt(dropoffVendorStatus);
        parcel.writeInt(pickupLocation);
        parcel.writeString(deliveryDate);
        parcel.writeInt(status);
        parcel.writeString(pickupDate);
        parcel.writeInt(pickupVendorStatus);
        parcel.writeInt(vendorLocation);
        parcel.writeInt(unloading);
        parcel.writeInt(dropOffLocation);
        parcel.writeString(dropOffPhone);
        parcel.writeString(comments);
        parcel.writeString(truckSize);
        parcel.writeInt(roundTrip);
        parcel.writeString(freightLine);
        parcel.writeString(pickupName);
        parcel.writeInt(projectId);
        parcel.writeString(dropOffName);
        parcel.writeString(pickupPhone);
        parcel.writeString(dropoffTime);
        parcel.writeString(pickupTime);
        parcel.writeInt(transferId);
        parcel.writeTypedList(equipment);
        parcel.writeString(pickupArriveTime);
        parcel.writeString(pickupLoadTime);

        parcel.writeByte((byte) (interofficeTransfer ? 1 : 0));
        parcel.writeString(pickupLocationName);
        parcel.writeString(dropoffLocationName);

        parcel.writeString(pickupDepartureTime);
        parcel.writeString(dropoffArriveTime);
        parcel.writeString(dropoffLoadTime);
        parcel.writeString(dropoffDepartureTime);
    }


/*  public Calendar getPickUpCalendar() {
        return pickUpCalendar;
    }

    public void setPickUpCalendar(Calendar pickUpCalendar) {
        this.pickUpCalendar = pickUpCalendar;
    }

    public Calendar getDropoffCalendar() {
        return dropoffCalendar;
    }

    public void setDropoffCalendar(Calendar dropoffCalendar) {
        this.dropoffCalendar = dropoffCalendar;
    }
*//*
    public Contacts getPickUpContacts() {
        return pickUpContacts;
    }

    public void setPickUpContacts(Contacts pickUpContacts) {
        this.pickUpContacts = pickUpContacts;
    }

    public Contacts getDropOffContacts() {
        return dropOffContacts;
    }

    public void setDropOffContacts(Contacts dropOffContacts) {
        this.dropOffContacts = dropOffContacts;
    }*/

    public String getDropoffDepartureTime() {
        return dropoffDepartureTime;
    }

    public void setDropoffDepartureTime(String dropoffDepartureTime) {
        this.dropoffDepartureTime = dropoffDepartureTime;
    }

    public String getDropoffLoadTime() {
        return dropoffLoadTime;
    }

    public void setDropoffLoadTime(String dropoffLoadTime) {
        this.dropoffLoadTime = dropoffLoadTime;
    }

    public String getDropoffArriveTime() {
        return dropoffArriveTime;
    }

    public void setDropoffArriveTime(String dropoffArriveTime) {
        this.dropoffArriveTime = dropoffArriveTime;
    }

    public String getPickupDepartureTime() {
        return pickupDepartureTime;
    }

    public void setPickupDepartureTime(String pickupDepartureTime) {
        this.pickupDepartureTime = pickupDepartureTime;
    }

    public String getPickupLoadTime() {
        return pickupLoadTime;
    }

    public void setPickupLoadTime(String pickupLoadTime) {
        this.pickupLoadTime = pickupLoadTime;
    }

    public String getPickupArriveTime() {
        return pickupArriveTime;
    }

    public void setPickupArriveTime(String pickupArriveTime) {
        this.pickupArriveTime = pickupArriveTime;
    }
}

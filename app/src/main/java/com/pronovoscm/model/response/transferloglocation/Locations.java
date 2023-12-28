package com.pronovoscm.model.response.transferloglocation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Locations implements Serializable, Parcelable {
    @SerializedName("dropoff_id")
    private int dropoffId;
    @SerializedName("drop_off_location")
    private String dropOffLocation;
    @SerializedName("pickup_id")
    private int pickupId;

    public Locations() {
    }

    @SerializedName("pick_up_location")
    private String pickUpLocation;

    protected Locations(Parcel in) {

        dropoffId = in.readInt();
        dropOffLocation = in.readString();
        pickupId = in.readInt();
        pickUpLocation = in.readString();
    }

    public static final Creator<Locations> CREATOR = new Creator<Locations>() {
        @Override
        public Locations createFromParcel(Parcel in) {
            return new Locations(in);
        }

        @Override
        public Locations[] newArray(int size) {
            return new Locations[size];
        }
    };

    public int getDropoffId() {
        return dropoffId;
    }

    public void setDropoffId(int dropoffId) {
        this.dropoffId = dropoffId;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public int getPickupId() {
        return pickupId;
    }

    public void setPickupId(int pickupId) {
        this.pickupId = pickupId;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(dropoffId);
        parcel.writeString(dropOffLocation);
        parcel.writeInt(pickupId);
        parcel.writeString(pickUpLocation);
    }
}

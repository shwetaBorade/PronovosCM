package com.pronovoscm.model.response.transferoverviewcount;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class TransferOverviewCountResponse implements Serializable, Parcelable {
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    protected TransferOverviewCountResponse(Parcel in) {
        message = in.readString();
        status = in.readInt();
    }

    public static final Creator<TransferOverviewCountResponse> CREATOR = new Creator<TransferOverviewCountResponse>() {
        @Override
        public TransferOverviewCountResponse createFromParcel(Parcel in) {
            return new TransferOverviewCountResponse(in);
        }

        @Override
        public TransferOverviewCountResponse[] newArray(int size) {
            return new TransferOverviewCountResponse[size];
        }
    };

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeInt(status);
    }



}

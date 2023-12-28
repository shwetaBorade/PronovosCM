package com.pronovoscm.model.response.transferlocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TransferLocationVendorResponse {

    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

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

    public class Data {
        @SerializedName("responseMsg")
        private String responsemsg;
        @SerializedName("responseCode")
        private int responsecode;
        @SerializedName("locations")
        private List<Locations> locations;

        public String getResponsemsg() {
            return responsemsg;
        }

        public void setResponsemsg(String responsemsg) {
            this.responsemsg = responsemsg;
        }

        public int getResponsecode() {
            return responsecode;
        }

        public void setResponsecode(int responsecode) {
            this.responsecode = responsecode;
        }

        public List<Locations> getLocations() {
            return locations;
        }

        public void setLocations(List<Locations> locations) {
            this.locations = locations;
        }
    }

    public class Locations implements Serializable {
        @SerializedName("phone")
        private String phone;
        @SerializedName("address")
        private String address;
        @SerializedName("vendor_name")
        private String vendorName;
        @SerializedName("location_id")
        private int locationId;
        @SerializedName("vendor_id")
        private int vendorId;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public int getLocationId() {
            return locationId;
        }

        public void setLocationId(int locationId) {
            this.locationId = locationId;
        }

        public int getVendorId() {
            return vendorId;
        }

        public void setVendorId(int vendorId) {
            this.vendorId = vendorId;
        }
    }
}

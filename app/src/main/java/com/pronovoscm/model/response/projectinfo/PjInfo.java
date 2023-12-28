package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.SerializedName;

public class PjInfo {
    @SerializedName("ups_zip")
    private String upsZip;
    @SerializedName("ups_state")
    private String upsState;
    @SerializedName("ups_city")
    private String upsCity;
    @SerializedName("ups_address2")
    private String upsAddress2;
    @SerializedName("ups_address")
    private String upsAddress;
    @SerializedName("ups_delivery")
    private String upsDelivery;
    @SerializedName("sdi_notes")
    private String sdiNotes;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("zip")
    private String zip;
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("address2")
    private String address2;
    @SerializedName("address")
    private String address;
    @SerializedName("project_number")
    private String projectNumber;
    @SerializedName("name")
    private String name;

    public String getUpsZip() {
        return upsZip;
    }

    public void setUpsZip(String upsZip) {
        this.upsZip = upsZip;
    }

    public String getUpsState() {
        return upsState;
    }

    public void setUpsState(String upsState) {
        this.upsState = upsState;
    }

    public String getUpsCity() {
        return upsCity;
    }

    public void setUpsCity(String upsCity) {
        this.upsCity = upsCity;
    }

    public String getUpsAddress2() {
        return upsAddress2;
    }

    public void setUpsAddress2(String upsAddress2) {
        this.upsAddress2 = upsAddress2;
    }

    public String getUpsAddress() {
        return upsAddress;
    }

    public void setUpsAddress(String upsAddress) {
        this.upsAddress = upsAddress;
    }

    public String getUpsDelivery() {
        return upsDelivery;
    }

    public void setUpsDelivery(String upsDelivery) {
        this.upsDelivery = upsDelivery;
    }

    public String getSdiNotes() {
        return sdiNotes;
    }

    public void setSdiNotes(String sdiNotes) {
        this.sdiNotes = sdiNotes;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

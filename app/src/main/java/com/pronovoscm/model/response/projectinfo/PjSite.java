package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.SerializedName;

public class PjSite {
    @SerializedName("hiring_notes")
    private String hiringNotes;
    @SerializedName("special_hiring")
    private String specialHiring;
    @SerializedName("site_notes")
    private String siteNotes;
    @SerializedName("special_site")
    private String specialSite;
    @SerializedName("drug_test")
    private String drugTest;
    @SerializedName("gc_orientation_notes")
    private String gcOrientationNotes;
    @SerializedName("gc_orientation")
    private String gcOrientation;
    @SerializedName("project_start_time")
    private String projectStartTime;
    @SerializedName("parking_location")
    private String parkingLocation;
    @SerializedName("camera_credentials")
    private String cameraCredentials;
    @SerializedName("camera_link")
    private String cameraLink;

    public String getHiringNotes() {
        return hiringNotes;
    }

    public void setHiringNotes(String hiringNotes) {
        this.hiringNotes = hiringNotes;
    }

    public String getSpecialHiring() {
        return specialHiring;
    }

    public void setSpecialHiring(String specialHiring) {
        this.specialHiring = specialHiring;
    }

    public String getSiteNotes() {
        return siteNotes;
    }

    public void setSiteNotes(String siteNotes) {
        this.siteNotes = siteNotes;
    }

    public String getSpecialSite() {
        return specialSite;
    }

    public void setSpecialSite(String specialSite) {
        this.specialSite = specialSite;
    }

    public String getDrugTest() {
        return drugTest;
    }

    public void setDrugTest(String drugTest) {
        this.drugTest = drugTest;
    }

    public String getGcOrientationNotes() {
        return gcOrientationNotes;
    }

    public void setGcOrientationNotes(String gcOrientationNotes) {
        this.gcOrientationNotes = gcOrientationNotes;
    }

    public String getGcOrientation() {
        return gcOrientation;
    }

    public void setGcOrientation(String gcOrientation) {
        this.gcOrientation = gcOrientation;
    }

    public String getProjectStartTime() {
        return projectStartTime;
    }

    public void setProjectStartTime(String projectStartTime) {
        this.projectStartTime = projectStartTime;
    }

    public String getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(String parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public String getCameraCredentials() {
        return cameraCredentials;
    }

    public void setCameraCredentials(String cameraCredentials) {
        this.cameraCredentials = cameraCredentials;
    }

    public String getCameraLink() {
        return cameraLink;
    }

    public void setCameraLink(String cameraLink) {
        this.cameraLink = cameraLink;
    }
}

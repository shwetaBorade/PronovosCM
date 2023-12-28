package com.pronovoscm.model.response.projects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Projects implements Serializable {
    @SerializedName("updated_at")
    private String UpdatedAt;
    @SerializedName("created_at")
    private String CreatedAt;
    @SerializedName("is_archived")
    private int IsArchived;
    @SerializedName("zip")
    private String Zip;
    @SerializedName("city")
    private String City;
    @SerializedName("state")
    private String State;
    @SerializedName("address")
    private String Address;
    @SerializedName("project_number")
    private String ProjectNumber;
    @SerializedName("name")
    private String Name;
    @SerializedName("pj_projects_id")
    private int PjProjectsId;
    @SerializedName("showcase_photo")
    private String showcasePhoto;

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public int getIsArchived() {
        return IsArchived;
    }

    public void setIsArchived(int IsArchived) {
        this.IsArchived = IsArchived;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String Zip) {
        this.Zip = Zip;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getProjectNumber() {
        return ProjectNumber;
    }

    public void setProjectNumber(String ProjectNumber) {
        this.ProjectNumber = ProjectNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getPjProjectsId() {
        return PjProjectsId;
    }

    public void setPjProjectsId(int PjProjectsId) {
        this.PjProjectsId = PjProjectsId;
    }

    public String getShowcasePhoto() {
        return showcasePhoto;
    }

    public void setShowcasePhoto(String showcasePhoto) {
        this.showcasePhoto = showcasePhoto;
    }
}

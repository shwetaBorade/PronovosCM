package com.pronovoscm.model.response.regions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Regions implements Serializable {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("active")
    private int active;
    @SerializedName("name")
    private String name;
    @SerializedName("regions_id")
    private int regionsId;

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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegionsId() {
        return regionsId;
    }

    public void setRegionsId(int regionsId) {
        this.regionsId = regionsId;
    }
}

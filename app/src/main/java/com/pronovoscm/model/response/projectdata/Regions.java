package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;

public class Regions {
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("active")
    private int active;
    @SerializedName("name")
    private String name;
    @SerializedName("regions_id")
    private int regions_id;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public int getRegions_id() {
        return regions_id;
    }

    public void setRegions_id(int regions_id) {
        this.regions_id = regions_id;
    }
}

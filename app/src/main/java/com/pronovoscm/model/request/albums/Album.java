package com.pronovoscm.model.request.albums;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("created_at")
    private String created_at;
    @SerializedName("deleted_at")
    private String deleted_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("pj_photos_folders_id_mobile")
    private String pj_photos_folders_id_mobile;
    @SerializedName("pj_photos_folders_id")
    private String pj_photos_folders_id;
    @SerializedName("name")
    private String name;

    public Album(String created_at, String deleted_at, String updated_at, String pj_photos_folders_id_mobile, String pj_photos_folders_id, String name) {
        this.created_at = created_at;
        this.deleted_at = deleted_at;
        this.updated_at = updated_at;
        this.pj_photos_folders_id_mobile = pj_photos_folders_id_mobile;
        this.pj_photos_folders_id = pj_photos_folders_id;
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPj_photos_folders_id_mobile() {
        return pj_photos_folders_id_mobile;
    }

    public void setPj_photos_folders_id_mobile(String pj_photos_folders_id_mobile) {
        this.pj_photos_folders_id_mobile = pj_photos_folders_id_mobile;
    }

    public String getPj_photos_folders_id() {
        return pj_photos_folders_id;
    }

    public void setPj_photos_folders_id(String pj_photos_folders_id) {
        this.pj_photos_folders_id = pj_photos_folders_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

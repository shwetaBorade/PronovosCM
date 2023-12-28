package com.pronovoscm.model.response.album;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Albums implements Serializable {
    @SerializedName("coverphoto")
    private Coverphoto coverphoto;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("pj_photos_folders_id_mobile")
    private int pjPhotosFoldersIdMobile;
    @SerializedName("name")
    private String name;
    @SerializedName("pj_photos_id")
    private int pjPhotosId;
    @SerializedName("is_static")
    private int isStatic;
    @SerializedName("pj_photos_folders_id")
    private int pjPhotosFoldersId;

    public int getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(int isStatic) {
        this.isStatic = isStatic;
    }

    public Coverphoto getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(Coverphoto coverphoto) {
        this.coverphoto = coverphoto;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPjPhotosFoldersIdMobile() {
        return pjPhotosFoldersIdMobile;
    }

    public void setPjPhotosFoldersIdMobile(int pjPhotosFoldersIdMobile) {
        this.pjPhotosFoldersIdMobile = pjPhotosFoldersIdMobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPjPhotosId() {
        return pjPhotosId;
    }

    public void setPjPhotosId(int pjPhotosId) {
        this.pjPhotosId = pjPhotosId;
    }

    public int getPjPhotosFoldersId() {
        return pjPhotosFoldersId;
    }

    public void setPjPhotosFoldersId(int pjPhotosFoldersId) {
        this.pjPhotosFoldersId = pjPhotosFoldersId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

package com.pronovoscm.model.response.album;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Coverphoto implements Serializable {
    @SerializedName("pj_photos_folders_id")
    private int pjPhotosFoldersId;
    @SerializedName("photo_location")
    private String photoLocation;
    @SerializedName("photo_name")
    private String photoName;
    @SerializedName("pj_photos_id")
    private int pjPhotosId;

    public int getPjPhotosFoldersId() {
        return pjPhotosFoldersId;
    }

    public void setPjPhotosFoldersId(int pjPhotosFoldersId) {
        this.pjPhotosFoldersId = pjPhotosFoldersId;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public int getPjPhotosId() {
        return pjPhotosId;
    }

    public void setPjPhotosId(int pjPhotosId) {
        this.pjPhotosId = pjPhotosId;
    }
}

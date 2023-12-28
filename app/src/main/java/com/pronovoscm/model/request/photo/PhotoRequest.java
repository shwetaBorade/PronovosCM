package com.pronovoscm.model.request.photo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoRequest {
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("min_photo_id")
    private int minPhotoId;
    @SerializedName("photos")
    private List<Photo> photos;

    public PhotoRequest(int albumId) {
        this.albumId = albumId;
        this.photos = photos;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getMinPhotoId() {
        return minPhotoId;
    }

    public void setMinPhotoId(int minPhotoId) {
        this.minPhotoId = minPhotoId;
    }
}

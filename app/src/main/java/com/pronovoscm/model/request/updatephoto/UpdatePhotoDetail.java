package com.pronovoscm.model.request.updatephoto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdatePhotoDetail {

    @SerializedName("photo_tags")
    private List<Photo_tags> photo_tags;
    @SerializedName("photo_description")
    private String photo_description;
    @SerializedName("photo_id")
    private int photo_id;
    @SerializedName("album_id")
    private int album_id;

    public List<Photo_tags> getPhoto_tags() {
        return photo_tags;
    }

    public void setPhoto_tags(List<Photo_tags> photo_tags) {
        this.photo_tags = photo_tags;
    }

    public String getPhoto_description() {
        return photo_description;
    }

    public void setPhoto_description(String photo_description) {
        this.photo_description = photo_description;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }
}

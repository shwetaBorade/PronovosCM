package com.pronovoscm.model.request.updatephoto;

import com.google.gson.annotations.SerializedName;

public class UpdatePhotoDetail2 {

    @SerializedName("photo_id")
    private int photo_id;
    @SerializedName("album_id")
    private int album_id;


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

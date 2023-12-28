package com.pronovoscm.model.request.photo;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.request.updatephoto.Photo_tags;

import java.util.List;

public class Photo {

    @SerializedName("photo_description")
    private String photo_description;
    @SerializedName("photo_name")
    private String photo_name;
    @SerializedName("photo_location")
    private String photo_location;
    @SerializedName("date_taken")
    private String date_taken;
    @SerializedName("photo_tags")
    private List<Photo_tags> photo_tags;
    @SerializedName("pj_photos_id")
    private int pj_photos_id;
    @SerializedName("album_id")
    private long album_id;
    @SerializedName("pj_photos_id_mobile")
    private long pj_photos_id_mobile;
    @SerializedName("photo_size")
    private String photo_size;

    @SerializedName("deleted_at")
    private String deletedAt;

    public String getPhoto_description() {
        return photo_description;
    }

    public void setPhoto_description(String photo_description) {
        this.photo_description = photo_description;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public String getPhoto_location() {
        return photo_location;
    }

    public void setPhoto_location(String photo_location) {
        this.photo_location = photo_location;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public List<Photo_tags> getPhoto_tags() {
        return photo_tags;
    }

    public void setPhoto_tags(List<Photo_tags> photo_tags) {
        this.photo_tags = photo_tags;
    }

    public int getPj_photos_id() {
        return pj_photos_id;
    }

    public void setPj_photos_id(int pj_photos_id) {
        this.pj_photos_id = pj_photos_id;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public long getPj_photos_id_mobile() {
        return pj_photos_id_mobile;
    }

    public void setPj_photos_id_mobile(long pj_photos_id_mobile) {
        this.pj_photos_id_mobile = pj_photos_id_mobile;
    }

    public String getPhoto_size() {
        return photo_size;
    }

    public void setPhoto_size(String photo_size) {
        this.photo_size = photo_size;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

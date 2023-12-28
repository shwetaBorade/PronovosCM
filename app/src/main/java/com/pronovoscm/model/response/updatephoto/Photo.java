package com.pronovoscm.model.response.updatephoto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Photo {
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("date_taken")
    private String date_taken;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("uploaded_by")
    private String uploaded_by;
    @SerializedName("users_id")
    private int users_id;
    @SerializedName("description")
    private String description;
    @SerializedName("tags")
    private List<UpdatedTag> tags;
    @SerializedName("pj_photos_id_mobile")
    private Long pj_photos_id_mobile;
    @SerializedName("photo_thumb")
    private String photo_thumb;
    @SerializedName("photo_location")
    private String photo_location;
    @SerializedName("photo_name")
    private String photo_name;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UpdatedTag> getTags() {
        return tags;
    }

    public void setTags(List<UpdatedTag> tags) {
        this.tags = tags;
    }

    public Long getPj_photos_id_mobile() {
        return pj_photos_id_mobile;
    }

    public void setPj_photos_id_mobile(Long pj_photos_id_mobile) {
        this.pj_photos_id_mobile = pj_photos_id_mobile;
    }

    public String getPhoto_thumb() {
        return photo_thumb;
    }

    public void setPhoto_thumb(String photo_thumb) {
        this.photo_thumb = photo_thumb;
    }

    public String getPhoto_location() {
        return photo_location;
    }

    public void setPhoto_location(String photo_location) {
        this.photo_location = photo_location;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }
}

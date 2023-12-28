package com.pronovoscm.model.response.updatephoto;

import com.google.gson.annotations.SerializedName;

public class UpdatedTag {


    @SerializedName("deleted_at")
    private String deleted_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("tag_name")
    private String tag_name;
    @SerializedName("taggable_id")
    private int taggable_id;
    @SerializedName("tag_id")
    private int tag_id;

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public int getTaggable_id() {
        return taggable_id;
    }

    public void setTaggable_id(int taggable_id) {
        this.taggable_id = taggable_id;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }
}

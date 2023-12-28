package com.pronovoscm.model.response.photo;

import com.google.gson.annotations.SerializedName;

public class PhotoTag {

    @SerializedName("user_id")
    private int user_id;
    @SerializedName("tag_name")
    private String tag_name;
    @SerializedName("taggable_id")
    private int taggable_id;
    @SerializedName("tag_id")
    private int tag_id;

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

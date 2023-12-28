package com.pronovoscm.model.request.updatephoto;

import com.google.gson.annotations.SerializedName;

public class Photo_tags {
    @SerializedName("keyword")
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

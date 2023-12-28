package com.pronovoscm.model.response.tag;

import com.google.gson.annotations.SerializedName;

public class TagsResponse {

    @SerializedName("data")
    private TagData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;


    public TagData getData() {
        return data;
    }

    public void setData(TagData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

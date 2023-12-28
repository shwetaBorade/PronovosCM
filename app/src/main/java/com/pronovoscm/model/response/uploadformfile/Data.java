package com.pronovoscm.model.response.uploadformfile;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("original_filename")
    private String originalFilename;
    @SerializedName("url")
    private String url;

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

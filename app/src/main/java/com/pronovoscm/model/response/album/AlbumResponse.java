package com.pronovoscm.model.response.album;

import com.google.gson.annotations.SerializedName;

public class AlbumResponse {

    @SerializedName("data")
    private AlbumData albumData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public AlbumData getAlbumData() {
        return albumData;
    }

    public void setAlbumData(AlbumData albumData) {
        this.albumData = albumData;
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

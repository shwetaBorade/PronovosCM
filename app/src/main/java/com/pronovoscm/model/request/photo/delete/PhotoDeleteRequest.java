package com.pronovoscm.model.request.photo.delete;

import com.google.gson.annotations.SerializedName;

public class PhotoDeleteRequest {
    @SerializedName("photo_id")
    private int photosId;

    public PhotoDeleteRequest(int photosId) {
        this.photosId = photosId;
    }
}

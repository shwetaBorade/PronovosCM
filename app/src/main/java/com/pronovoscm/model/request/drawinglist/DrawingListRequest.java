package com.pronovoscm.model.request.drawinglist;

import com.google.gson.annotations.SerializedName;

public class DrawingListRequest {

    @SerializedName("folder_id")
    private int folder_id;

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }
}

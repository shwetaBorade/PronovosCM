package com.pronovoscm.model.response.formarea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectAreas {

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("data")
    @Expose
    private String data;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

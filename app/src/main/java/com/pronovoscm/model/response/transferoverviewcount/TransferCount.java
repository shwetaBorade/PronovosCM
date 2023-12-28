package com.pronovoscm.model.response.transferoverviewcount;

import com.google.gson.annotations.SerializedName;

public class TransferCount {
    @SerializedName("title")
    private String title;
    @SerializedName("count")
    private int count;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

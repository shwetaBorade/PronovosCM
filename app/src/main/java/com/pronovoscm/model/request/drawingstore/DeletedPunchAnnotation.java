package com.pronovoscm.model.request.drawingstore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class DeletedPunchAnnotation {

    @SerializedName("rect")
    private String rect;
    @SerializedName("title")
    private String title;
    @SerializedName("punch_number")
    private int punch_number;
    @SerializedName("punch_status")
    private int punch_status;
    @SerializedName("punch_id_mobile")
    private long punch_id_mobile;
    @SerializedName("punch_id")
    private int punch_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPunch_number() {
        return punch_number;
    }

    public void setPunch_number(int punch_number) {
        this.punch_number = punch_number;
    }

    public int getPunch_status() {
        return punch_status;
    }

    public void setPunch_status(int punch_status) {
        this.punch_status = punch_status;
    }

    public long getPunch_id_mobile() {
        return punch_id_mobile;
    }

    public void setPunch_id_mobile(long punch_id_mobile) {
        this.punch_id_mobile = punch_id_mobile;
    }

    public int getPunch_id() {
        return punch_id;
    }

    public void setPunch_id(int punch_id) {
        this.punch_id = punch_id;
    }

    public String getRect() {
        return rect;
    }

    public void setRect(String rect) {
        this.rect = rect;
    }
}

package com.pronovoscm.model.response.syncupdate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncUpdateData {
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("drawings")
    private List<Drawings> drawings;
    @SerializedName("has_update")
    private int has_update;
    @SerializedName("count")
    private int count;

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<Drawings> getDrawings() {
        return drawings;
    }

    public void setDrawings(List<Drawings> drawings) {
        this.drawings = drawings;
    }

    public int getHas_update() {
        return has_update;
    }

    public void setHas_update(int has_update) {
        this.has_update = has_update;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

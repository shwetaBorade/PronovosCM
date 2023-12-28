package com.pronovoscm.model.response.drawinglist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrawingListData {
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("drwings")
    private List<Drwings> drwings;
    @SerializedName("drw_folders_id")
    private int drw_folders_id;

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

    public List<Drwings> getDrwings() {
        return drwings;
    }

    public void setDrwings(List<Drwings> drwings) {
        this.drwings = drwings;
    }

    public int getDrw_folders_id() {
        return drw_folders_id;
    }

    public void setDrw_folders_id(int drw_folders_id) {
        this.drw_folders_id = drw_folders_id;
    }
}

package com.pronovoscm.model.response.drawingpunchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("drw_punchlists")
    private List<DrwPunchlists> drwPunchlists;

    public String getResponsemsg() {
        return responsemsg;
    }

    public void setResponsemsg(String responsemsg) {
        this.responsemsg = responsemsg;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public List<DrwPunchlists> getDrwPunchlists() {
        return drwPunchlists;
    }

    public void setDrwPunchlists(List<DrwPunchlists> drwPunchlists) {
        this.drwPunchlists = drwPunchlists;
    }
}

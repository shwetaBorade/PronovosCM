package com.pronovoscm.model.response.regions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("regions")
    private List<Regions> regions;

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

    public List<Regions> getRegions() {
        return regions;
    }

    public void setRegions(List<Regions> regions) {
        this.regions = regions;
    }
}

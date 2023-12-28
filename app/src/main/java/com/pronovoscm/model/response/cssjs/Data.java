package com.pronovoscm.model.response.cssjs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("form_assets")
    private List<FormAsset> formAssets;

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

    public List<FormAsset> getFormAssets() {
        return formAssets;
    }

    public void setFormAssets(List<FormAsset> formAssets) {
        this.formAssets = formAssets;
    }
}

package com.pronovoscm.model.response.photo.delete;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;

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
}

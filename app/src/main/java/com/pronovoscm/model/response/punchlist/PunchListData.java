package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PunchListData {

    @SerializedName("punchlists")
    private List<Punchlist> punchlist;

    @SerializedName("responseCode")
    private int responseCode;

    @SerializedName("responseMsg")
    private String responseMsg;

    public void setPunchlists(List<Punchlist> punchlists) {
        this.punchlist = punchlists;
    }

    public List<Punchlist> getPunchlists() {
        return punchlist;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    @Override
    public String toString() {
        return "WorkDetailsData{" +
                "punchlists = '" + punchlist + '\'' +
                ",responseCode = '" + responseCode + '\'' +
                ",responseMsg = '" + responseMsg + '\'' +
                "}";
    }
}


package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PunchListHistoryData {

    @SerializedName("punchlists")
    private List<PunchListHistory> punchListHistories;

    @SerializedName("responseCode")
    private int responseCode;

    @SerializedName("responseMsg")
    private String responseMsg;

    public List<PunchListHistory> getPunchListHistories() {
        return punchListHistories;
    }

    public void setPunchListHistories(List<PunchListHistory> punchListHistories) {
        this.punchListHistories = punchListHistories;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}

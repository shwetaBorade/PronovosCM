package com.pronovoscm.model.response.cclist;

import com.google.gson.annotations.SerializedName;

public class CCListResponse {

    @SerializedName("data")
    private CCListData mCCListData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public CCListData getCCListData() {
        return mCCListData;
    }

    public void setCCListData(CCListData CCListData) {
        mCCListData = CCListData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

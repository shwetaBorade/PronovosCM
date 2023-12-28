package com.pronovoscm.model.response.companylist;

import com.google.gson.annotations.SerializedName;

public class CompanyListResponse {

    @SerializedName("data")
    private CompanyListData mCompanyListData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public CompanyListData getData() {
        return mCompanyListData;
    }

    public void setData(CompanyListData data) {
        this.mCompanyListData = data;
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

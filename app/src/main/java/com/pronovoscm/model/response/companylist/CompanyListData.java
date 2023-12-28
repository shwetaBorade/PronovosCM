package com.pronovoscm.model.response.companylist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyListData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("companies")
    private List<Companies> companies;

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

    public List<Companies> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Companies> companies) {
        this.companies = companies;
    }
}

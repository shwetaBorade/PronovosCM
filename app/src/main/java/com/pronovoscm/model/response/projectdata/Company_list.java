package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.companylist.Companies;

import java.util.List;

public class Company_list {
    @SerializedName("companies")
    private List<com.pronovoscm.model.response.companylist.Companies> companies;
    @SerializedName("project_id")
    private int project_id;


    public List<com.pronovoscm.model.response.companylist.Companies> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Companies> companies) {
        this.companies = companies;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }
}

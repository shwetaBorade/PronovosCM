package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.regions.Regions;

import java.util.List;

public class Data {

    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("assigned_to")
    private List<Assigned_to> assigned_to;
    @SerializedName("company_list")
    private List<Company_list> company_list;
    @SerializedName("regions")
    private List<com.pronovoscm.model.response.regions.Regions> regions;
    @SerializedName("projects")
    private List<Projects> projects;

    public List<Projects> getProjects() {
        return projects;
    }

    public void setProjects(List<Projects> projects) {
        this.projects = projects;
    }

    public List<Company_list> getCompany_list() {
        return company_list;
    }

    public void setCompany_list(List<Company_list> company_list) {
        this.company_list = company_list;
    }

    public List<Regions> getRegions() {
        return regions;
    }

    public void setRegions(List<com.pronovoscm.model.response.regions.Regions> regions) {
        this.regions = regions;
    }

    public List<Assigned_to> getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(List<Assigned_to> assigned_to) {
        this.assigned_to = assigned_to;
    }

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

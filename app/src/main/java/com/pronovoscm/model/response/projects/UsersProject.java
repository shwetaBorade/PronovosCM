package com.pronovoscm.model.response.projects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsersProject {
    @SerializedName("responseMsg")
    private String Responsemsg;
    @SerializedName("responseCode")
    private int Responsecode;
    @SerializedName("projects")
    private List<Projects> Projects;
    @SerializedName("region_id")
    private String RegionId;
    @SerializedName("newversionAvailable")
    private Boolean newversionAvailable;

    public Boolean getNewversionAvailable() {
        if (newversionAvailable != null)
            return newversionAvailable;
        else return false;
    }

    public void setNewversionAvailable(Boolean newversionAvailable) {
        this.newversionAvailable = newversionAvailable;
    }

    public String getResponsemsg() {
        return Responsemsg;
    }

    public void setResponsemsg(String Responsemsg) {
        this.Responsemsg = Responsemsg;
    }

    public int getResponsecode() {
        return Responsecode;
    }

    public void setResponsecode(int Responsecode) {
        this.Responsecode = Responsecode;
    }

    public List<Projects> getProjects() {
        return Projects;
    }

    public void setProjects(List<Projects> Projects) {
        this.Projects = Projects;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String RegionId) {
        this.RegionId = RegionId;
    }

    @Override
    public String toString() {
        return "UsersProject{" +
                "Responsemsg='" + Responsemsg + '\'' +
                ", Responsecode=" + Responsecode +
                ", Projects=" + Projects +
                ", RegionId='" + RegionId + '\'' +
                ", newversionAvailable=" + newversionAvailable +
                '}';
    }
}

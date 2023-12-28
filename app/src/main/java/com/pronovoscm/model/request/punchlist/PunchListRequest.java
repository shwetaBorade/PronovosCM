package com.pronovoscm.model.request.punchlist;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.punchlist.Punchlist;

import java.util.List;

public class PunchListRequest {

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("punchlists")
    private List<PunchList> punchlists;

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setPunchlists(List<PunchList> punchlists) {
        this.punchlists = punchlists;
    }

    public List<PunchList> getPunchlists() {
        return punchlists;
    }

    @Override
    public String toString() {
        return "PunchListRequest{" +
                        "project_id = '" + projectId + '\'' +
                        ",punchlists = '" + punchlists + '\'' +
                        "}";
    }
}
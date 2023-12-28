package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.model.response.assignee.AssigneeList;

import java.util.List;

public class Assigned_to {
    @SerializedName("assignee_list")
    private List<AssigneeList> assignee_list;
    @SerializedName("project_id")
    private int project_id;

    public List<AssigneeList> getAssignee_list() {
        return assignee_list;
    }

    public void setAssignee_list(List<AssigneeList> assignee_list) {
        this.assignee_list = assignee_list;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }
}

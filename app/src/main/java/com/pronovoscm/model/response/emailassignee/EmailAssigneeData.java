package com.pronovoscm.model.response.emailassignee;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailAssigneeData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("assignee_list")
    private List<AssigneeList> assigneeList;

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

    public List<AssigneeList> getAssigneeList() {
        return assigneeList;
    }

    public void setAssigneeList(List<AssigneeList> assigneeList) {
        this.assigneeList = assigneeList;
    }
}

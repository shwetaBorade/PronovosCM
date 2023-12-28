package com.pronovoscm.model.response.assignee;

import com.google.gson.annotations.SerializedName;

public class AssigneeList {
    @SerializedName("active")
    private int active;
    @SerializedName("name")
    private String name;
    @SerializedName("users_id")
    private int usersId;

    @SerializedName("default_assignee")
    private int defaultAssignee;

    @SerializedName("default_cc")
    private int defaultCC;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public boolean isDefaultAssignee() {
        return (defaultAssignee == 0) ? false: true;
    }

    public void setDefaultAssignee(int defaultAssignee) {
        this.defaultAssignee = defaultAssignee;
    }

    public boolean isDefaultCC() {
        return (defaultCC == 0) ? false: true;
    }

    public void setDefaultCC(int defaultCC) {
        this.defaultCC = defaultCC;
    }
}

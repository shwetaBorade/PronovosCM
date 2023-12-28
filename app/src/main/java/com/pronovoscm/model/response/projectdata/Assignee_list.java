package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;

public class Assignee_list {
    @SerializedName("active")
    private int active;
    @SerializedName("name")
    private String name;
    @SerializedName("users_id")
    private int users_id;

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

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }
}

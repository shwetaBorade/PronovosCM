package com.pronovoscm.model.response.projects;

import com.google.gson.annotations.SerializedName;

public class ProjectResponse {

    @SerializedName("data")
    private UsersProject mUsersProject;
    @SerializedName("message")
    private String Message;
    @SerializedName("status")
    private int Status;

    public UsersProject getUsersProject() {
        return mUsersProject;
    }

    public void setUsersProject(UsersProject usersProject) {
        this.mUsersProject = usersProject;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
}

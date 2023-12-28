package com.pronovoscm.model.response.projectteam;

import com.google.gson.annotations.SerializedName;

public  class ProjectTeamResponse {
    @SerializedName("data")
    private TeamData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public TeamData getData() {
        return data;
    }

    public void setData(TeamData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.pronovoscm.model.response.resources;

import com.google.gson.annotations.SerializedName;

public class Users {
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("name")
    private String name;
    @SerializedName("is_primary_resource")
    private int isPrimaryResource;
    @SerializedName("status")
    private int status;
    @SerializedName("users_id")
    private int usersId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsPrimaryResource() {
        return isPrimaryResource;
    }

    public void setIsPrimaryResource(int isPrimaryResource) {
        this.isPrimaryResource = isPrimaryResource;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }
}

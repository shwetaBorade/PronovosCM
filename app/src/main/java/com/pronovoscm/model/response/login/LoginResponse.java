package com.pronovoscm.model.response.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private UserDetails data;
    @SerializedName("message")
    private String message;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserDetails getUserDetails() {
        return data;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.data = userDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

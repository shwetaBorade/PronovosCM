package com.pronovoscm.model.request.login;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("platform")
    String platform;
    @SerializedName("device_token")
    String device_token;
    @SerializedName("version")
    String version;

    public LoginRequest(String email, String password, String platform, String device_token, String version) {
        this.email = email;
        this.password = password;
        this.platform = platform;
        this.device_token = device_token;
        this.version = version;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

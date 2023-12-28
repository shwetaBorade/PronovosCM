package com.pronovoscm.model.request.forgetpassword;

import com.google.gson.annotations.SerializedName;

public class ForgetPasswordRequest {
    @SerializedName("email")
    String email;

    public ForgetPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

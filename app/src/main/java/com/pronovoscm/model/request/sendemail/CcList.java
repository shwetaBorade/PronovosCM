package com.pronovoscm.model.request.sendemail;

import com.google.gson.annotations.SerializedName;

public class CcList {

    public CcList(String email) {
        this.email = email;
    }

    public CcList() {

    }

    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

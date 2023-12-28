package com.pronovoscm.model.response.formuser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("user_forms")
    private List<UserForm> userForms;

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

    public List<UserForm> getUserForms() {
        return userForms;
    }

    public void setUserForms(List<UserForm> userForms) {
        this.userForms = userForms;
    }
}

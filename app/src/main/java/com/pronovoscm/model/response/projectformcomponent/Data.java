package com.pronovoscm.model.response.projectformcomponent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("form_component")
    private List<FormComponent> formComponent;

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

    public List<FormComponent> getFormComponent() {
        return formComponent;
    }

    public void setFormComponent(List<FormComponent> formComponent) {
        this.formComponent = formComponent;
    }
}

package com.pronovoscm.model.response.signedurl;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("formaction")
    private Formaction formaction;
    @SerializedName("formattributes")
    private Formattributes formattributes;

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Formaction getFormaction() {
        return formaction;
    }

    public void setFormaction(Formaction formaction) {
        this.formaction = formaction;
    }

    public Formattributes getFormattributes() {
        return formattributes;
    }

    public void setFormattributes(Formattributes formattributes) {
        this.formattributes = formattributes;
    }

    @Override
    public String toString() {
        return "Data{" +
                "formaction=" + formaction +
                ", formattributes=" + formattributes +
                '}';
    }
}

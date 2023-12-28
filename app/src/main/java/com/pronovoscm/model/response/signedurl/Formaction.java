package com.pronovoscm.model.response.signedurl;

import com.google.gson.annotations.SerializedName;

public class Formaction {
    @SerializedName("enctype")
    private String enctype;
    @SerializedName("method")
    private String method;
    @SerializedName("action")
    private String action;

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Formaction{" +
                "enctype='" + enctype + '\'' +
                ", method='" + method + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

package com.pronovoscm.model.response.emaildefaultsettings;

import com.google.gson.annotations.SerializedName;

public class DefaultSettingsResponse {

    @SerializedName("data")
    private DefaultSettingData data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public DefaultSettingData getData() {
        return data;
    }

    public void setData(DefaultSettingData data) {
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

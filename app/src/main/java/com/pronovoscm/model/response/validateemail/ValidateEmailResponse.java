package com.pronovoscm.model.response.validateemail;

import com.google.gson.annotations.SerializedName;

public class ValidateEmailResponse {

    @SerializedName("data")
    private ValidateEmailData mValidateEmailData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public ValidateEmailData getValidateEmailData() {
        return mValidateEmailData;
    }

    public void setValidateEmailData(ValidateEmailData validateEmailData) {
        mValidateEmailData = validateEmailData;
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

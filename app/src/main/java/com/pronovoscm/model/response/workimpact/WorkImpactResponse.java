package com.pronovoscm.model.response.workimpact;

import com.google.gson.annotations.SerializedName;

public class WorkImpactResponse {

    @SerializedName("data")
    private WorkImpactData mWorkImpactData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public WorkImpactData getWorkImpactData() {
        return mWorkImpactData;
    }

    public void setWorkImpactData(WorkImpactData workImpactData) {
        mWorkImpactData = workImpactData;
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

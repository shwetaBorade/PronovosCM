package com.pronovoscm.model.request.submitform;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class SubmitFormRequest {
    @SerializedName("submission")
    private List<Submission> submission;
    @SerializedName("platform")
    private String platform;

    public SubmitFormRequest() {
        platform="android";
    }

    public List<Submission> getSubmission() {
        return submission;
    }

    public void setSubmission(List<Submission> submission) {
        this.submission = submission;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}

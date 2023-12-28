package com.pronovoscm.model.response.submittals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubmittalsResponseListData {
    @SerializedName("submittals")
    @Expose
    private List<Submittals> submittals = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public List<Submittals> getSubmittals() {
        return submittals;
    }

    public void setSubmittals(List<Submittals> submittals) {
        this.submittals = submittals;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}

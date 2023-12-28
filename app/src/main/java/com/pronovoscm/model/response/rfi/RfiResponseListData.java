package com.pronovoscm.model.response.rfi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RfiResponseListData {
    @SerializedName("rfis")
    @Expose
    private List<Rfi> rfis = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;

    public List<Rfi> getRfis() {
        return rfis;
    }

    public void setRfis(List<Rfi> rfis) {
        this.rfis = rfis;
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

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

}

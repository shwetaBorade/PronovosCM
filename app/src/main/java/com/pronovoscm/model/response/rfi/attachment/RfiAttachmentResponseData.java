package com.pronovoscm.model.response.rfi.attachment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RfiAttachmentResponseData {
    @SerializedName("rfi_attachments")
    @Expose
    private List<RfiAttachment> rfiAttachments = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;

    public List<RfiAttachment> getRfiAttachments() {
        return rfiAttachments;
    }

    public void setRfiAttachments(List<RfiAttachment> rfiAttachments) {
        this.rfiAttachments = rfiAttachments;
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

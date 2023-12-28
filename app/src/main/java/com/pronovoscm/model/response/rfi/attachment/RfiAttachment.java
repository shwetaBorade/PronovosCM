package com.pronovoscm.model.response.rfi.attachment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiAttachment {
    @SerializedName("pj_rfi_attachments_id")
    @Expose
    private Integer pjRfiAttachmentsId;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;
    @SerializedName("pj_rfi_id")
    @Expose
    private Integer pjRfiId;
    @SerializedName("attach_path")
    @Expose
    private String attachPath;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("pj_rfi_replies_id")
    @Expose
    private Integer pjRfiRepliesId;
    @SerializedName("pj_rfi_origname")
    @Expose
    private String pjRfiOrigname;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPjRfiAttachmentsId() {
        return pjRfiAttachmentsId;
    }

    public void setPjRfiAttachmentsId(Integer pjRfiAttachmentsId) {
        this.pjRfiAttachmentsId = pjRfiAttachmentsId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getPjRfiId() {
        return pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getPjRfiRepliesId() {
        return pjRfiRepliesId;
    }

    public void setPjRfiRepliesId(Integer pjRfiRepliesId) {
        this.pjRfiRepliesId = pjRfiRepliesId;
    }

    public String getPjRfiOrigname() {
        return pjRfiOrigname;
    }

    public void setPjRfiOrigname(String pjRfiOrigname) {
        this.pjRfiOrigname = pjRfiOrigname;
    }
}

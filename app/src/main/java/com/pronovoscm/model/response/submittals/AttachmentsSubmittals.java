package com.pronovoscm.model.response.submittals;

import com.google.gson.annotations.SerializedName;

public class AttachmentsSubmittals {
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("attachments_id")
    private Integer attachments_id;
    @SerializedName("attach_path")
    private String attach_path;
    @SerializedName("original_name")
    private String originalName;
    @SerializedName("attachments_id_mobile")
    private String attachmentsIdMobile;

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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getAttachments_id() {
        return attachments_id;
    }

    public void setAttachments_id(Integer attachments_id) {
        this.attachments_id = attachments_id;
    }

    public String getAttach_path() {
        return attach_path;
    }

    public void setAttach_path(String attach_path) {
        this.attach_path = attach_path;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getAttachmentsIdMobile() {
        return attachmentsIdMobile;
    }

    public void setAttachmentsIdMobile(String attachmentsIdMobile) {
        this.attachmentsIdMobile = attachmentsIdMobile;
    }
}

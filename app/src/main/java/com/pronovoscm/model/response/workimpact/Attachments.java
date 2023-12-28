package com.pronovoscm.model.response.workimpact;

import com.google.gson.annotations.SerializedName;

public class Attachments {
    @SerializedName("attachments_id_mobile")
    private int attachmentsIdMobile;
    @SerializedName("original_name")
    private String originalName;
    @SerializedName("attach_path")
    private String attachPath;
    @SerializedName("attachments_id")
    private int attachmentsId;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("type")
    private String type;

    public int getAttachmentsIdMobile() {
        return attachmentsIdMobile;
    }

    public void setAttachmentsIdMobile(int attachmentsIdMobile) {
        this.attachmentsIdMobile = attachmentsIdMobile;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public int getAttachmentsId() {
        return attachmentsId;
    }

    public void setAttachmentsId(int attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

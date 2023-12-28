package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

public class Attachment {

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("attachments_id_mobile")
    private int attachmentsIdMobile;

    @SerializedName("attach_path")
    private String attachPath;

    @SerializedName("deleted_at")
    private String deletedAt;

    @SerializedName("attachments_id")
    private int attachmentsId;
    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setAttachmentsIdMobile(int attachmentsIdMobile) {
        this.attachmentsIdMobile = attachmentsIdMobile;
    }

    public int getAttachmentsIdMobile() {
        return attachmentsIdMobile;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setAttachmentsId(int attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public int getAttachmentsId() {
        return attachmentsId;
    }

    @Override
    public String toString() {
        return "AttachmentsItem{" +
                "original_name = '" + originalName + '\'' +
                ",attachments_id_mobile = '" + attachmentsIdMobile + '\'' +
                ",attach_path = '" + attachPath + '\'' +
                ",deleted_at = '" + deletedAt + '\'' +
                ",attachments_id = '" + attachmentsId + '\'' +
                "}";
    }
}

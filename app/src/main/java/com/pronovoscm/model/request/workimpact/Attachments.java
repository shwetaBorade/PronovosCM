package com.pronovoscm.model.request.workimpact;

import com.google.gson.annotations.SerializedName;

public class Attachments {
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("attachments_id_mobile")
    private int attachmentsIdMobile;
    @SerializedName("attach_path")
    private String attachPath;
    @SerializedName("attachments_id")
    private int attachmentsId;

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getAttachmentsIdMobile() {
        return attachmentsIdMobile;
    }

    public void setAttachmentsIdMobile(int attachmentsIdMobile) {
        this.attachmentsIdMobile = attachmentsIdMobile;
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
}

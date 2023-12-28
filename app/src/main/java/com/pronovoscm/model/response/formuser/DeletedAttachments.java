package com.pronovoscm.model.response.formuser;

import com.google.gson.annotations.SerializedName;

public class DeletedAttachments {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("users_id")
    private int usersId;
    @SerializedName("tenant_id")
    private int tenantId;
    @SerializedName("user_forms_id")
    private int userFormsId;
    @SerializedName("forms_id")
    private int formsId;
    @SerializedName("platform")
    private String platform;
    @SerializedName("filename")
    private String filename;
    @SerializedName("form_deleted_attachments_id")
    private int formDeletedAttachmentsId;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getUserFormsId() {
        return userFormsId;
    }

    public void setUserFormsId(int userFormsId) {
        this.userFormsId = userFormsId;
    }

    public int getFormsId() {
        return formsId;
    }

    public void setFormsId(int formsId) {
        this.formsId = formsId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFormDeletedAttachmentsId() {
        return formDeletedAttachmentsId;
    }

    public void setFormDeletedAttachmentsId(int formDeletedAttachmentsId) {
        this.formDeletedAttachmentsId = formDeletedAttachmentsId;
    }
}

package com.pronovoscm.model.response.drawingfolder;

import com.google.gson.annotations.SerializedName;

public class Drawingfolders {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("folder_description")
    private String folderDescription;
    @SerializedName("folder_name")
    private String folderName;
    @SerializedName("drw_folders_id")
    private int drwFoldersId;
    @SerializedName("deleted_at")
    private String deletedAt;

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

    public String getFolderDescription() {
        return folderDescription;
    }

    public void setFolderDescription(String folderDescription) {
        this.folderDescription = folderDescription;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getDrwFoldersId() {
        return drwFoldersId;
    }

    public void setDrwFoldersId(int drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

package com.pronovoscm.model.response.logresponse;

import com.google.gson.annotations.SerializedName;

public class DrawingLogs {
    @SerializedName("users_id")
    private String users_id;
    @SerializedName("comments")
    private String comments;
    @SerializedName("folder_name")
    private String folderName;
    @SerializedName("drw_folders_id")
    private String drwFoldersId;
    @SerializedName("file_location")
    private String fileLocation;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("drawing_logs_id")
    private String drawingLogsId;

    public String getUsers_id() {
        return users_id;
    }

    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getDrwFoldersId() {
        return drwFoldersId;
    }

    public void setDrwFoldersId(String drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    public String getDrawingLogsId() {
        return drawingLogsId;
    }

    public void setDrawingLogsId(String drawingLogsId) {
        this.drawingLogsId = drawingLogsId;
    }
}

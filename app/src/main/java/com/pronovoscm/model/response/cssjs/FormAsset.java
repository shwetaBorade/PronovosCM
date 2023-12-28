package com.pronovoscm.model.response.cssjs;

import com.google.gson.annotations.SerializedName;

public class FormAsset {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("file_path")
    private String filePath;
    @SerializedName("file_type")
    private String fileType;
    @SerializedName("file_name")
    private String fileName;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

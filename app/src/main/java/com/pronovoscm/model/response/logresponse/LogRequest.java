package com.pronovoscm.model.response.logresponse;

import com.google.gson.annotations.SerializedName;

import java.io.File;

//        user_id
//        comments
//        folder_name
//        folder_id
//        file
public class LogRequest {
    @SerializedName("folder_id")
    private String folder_id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("comments")
    private String comments;
    @SerializedName("folder_name")
    private String folder_name;
    @SerializedName("file")
    private File file;

    public String getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

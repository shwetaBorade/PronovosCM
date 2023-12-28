package com.pronovoscm.model.request.albums;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumRequest {

    @SerializedName("albums")
    private List<Album> albums;
    @SerializedName("project_id")
    private int projectId;

    public AlbumRequest(List<Album> albums, int projectId) {
        this.albums = albums;
        this.projectId = projectId;

    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

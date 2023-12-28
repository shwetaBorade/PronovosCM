package com.pronovoscm.model.response.album;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("albums")
    private List<Albums> albums;
    @SerializedName("pj_project_id")
    private int pjProjectId;
    @SerializedName("total_records")
    private int totalRecords;

    public String getResponsemsg() {
        return responsemsg;
    }

    public void setResponsemsg(String responsemsg) {
        this.responsemsg = responsemsg;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public List<Albums> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Albums> albums) {
        this.albums = albums;
    }

    public int getPjProjectId() {
        return pjProjectId;
    }

    public void setPjProjectId(int pjProjectId) {
        this.pjProjectId = pjProjectId;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}

package com.pronovoscm.model.response.updatephoto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdatePhotoDetailData {
    @SerializedName("responseMsg")
    private String responseMsg;
    @SerializedName("responseCode")
    private int responseCode;
    @SerializedName("photo")
    private List<Photo> photo;
    @SerializedName("pj_photos_id")
    private int pj_photos_id;
    @SerializedName("album_id")
    private int album_id;
    @SerializedName("total_records")
    private int total_records;

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Photo> photo) {
        this.photo = photo;
    }

    public int getPj_photos_id() {
        return pj_photos_id;
    }

    public void setPj_photos_id(int pj_photos_id) {
        this.pj_photos_id = pj_photos_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }
}

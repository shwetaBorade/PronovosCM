package com.pronovoscm.model.response.photo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PhotoResponse {
    @SerializedName("data")
    private PhotoData photoData;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public PhotoData getPhotoData() {
        return photoData;
    }

    public void setPhotoData(PhotoData photoData) {
        this.photoData = photoData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class PhotoData implements Serializable {
        @SerializedName("photos")
        private List<Photos> photos;
        @SerializedName("album_id")
        private int albumId;
        @SerializedName("responseCode")
        private int responseCode;
        @SerializedName("total_records")
        private int totalRecords;
        @SerializedName("responseMsg")
        private String responseMsg;

        public String getResponseMsg() {
            return responseMsg;
        }

        public void setResponseMsg(String responseMsg) {
            this.responseMsg = responseMsg;
        }

        public List<Photos> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Photos> photos) {
            this.photos = photos;
        }

        public int getAlbumId() {
            return albumId;
        }

        public void setAlbumId(int albumId) {
            this.albumId = albumId;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }
    }

    public static class Photos implements Serializable {

        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("date_taken")
        private String dateTaken;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("uploaded_by")
        private String uploadedBy;
        @SerializedName("users_id")
        private int usersId;
        @SerializedName("description")
        private String description;
        @SerializedName("deleted_at")
        private String deletedAt;
        @SerializedName("tags")
        private List<PhotoTag> tags;
        @SerializedName("pj_photos_id_mobile")
        private int pjPhotosIdMobile;
        @SerializedName("photo_thumb")
        private String photoThumb;
        @SerializedName("photo_location")
        private String photoLocation;
        @SerializedName("photo_name")
        private String photoName;
        @SerializedName("pj_photos_id")
        private int pjPhotosId;

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getDateTaken() {
            return dateTaken;
        }

        public void setDateTaken(String dateTaken) {
            this.dateTaken = dateTaken;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUploadedBy() {
            return uploadedBy;
        }

        public void setUploadedBy(String uploadedBy) {
            this.uploadedBy = uploadedBy;
        }

        public int getUsersId() {
            return usersId;
        }

        public void setUsersId(int usersId) {
            this.usersId = usersId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<PhotoTag> getTags() {
            return tags;
        }

        public void setTags(List<PhotoTag> tags) {
            this.tags = tags;
        }

        public int getPjPhotosIdMobile() {
            return pjPhotosIdMobile;
        }

        public void setPjPhotosIdMobile(int pjPhotosIdMobile) {
            this.pjPhotosIdMobile = pjPhotosIdMobile;
        }

        public String getPhotoThumb() {
            return photoThumb;
        }

        public void setPhotoThumb(String photoThumb) {
            this.photoThumb = photoThumb;
        }

        public String getPhotoLocation() {
            return photoLocation;
        }

        public void setPhotoLocation(String photoLocation) {
            this.photoLocation = photoLocation;
        }

        public String getPhotoName() {
            return photoName;
        }

        public void setPhotoName(String photoName) {
            this.photoName = photoName;
        }

        public int getPjPhotosId() {
            return pjPhotosId;
        }

        public void setPjPhotosId(int pjPhotosId) {
            this.pjPhotosId = pjPhotosId;
        }

        public String getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
        }
    }
}

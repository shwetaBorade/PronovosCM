package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "Album_cover_photo")
public class AlbumCoverPhoto {
    @Property(nameInDb = "photo_location")
    String photoLocation;
    @Property(nameInDb = "photo_name")
    String photoName;
    @Property(nameInDb = "pj_photos_id")
    Integer pjPhotoId;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "pj_photos_folder_id")
    Integer pjPhotosFolderId;
    @Index(unique = true)
    @Property(nameInDb = "pj_photos_folder_mobile_id")
    Integer pjPhotosFolderMobileId;

    @Generated(hash = 871119731)
    public AlbumCoverPhoto(String photoLocation, String photoName,
                           Integer pjPhotoId, Integer usersId, Integer pjProjectsId,
                           Integer pjPhotosFolderId, Integer pjPhotosFolderMobileId) {
        this.photoLocation = photoLocation;
        this.photoName = photoName;
        this.pjPhotoId = pjPhotoId;
        this.usersId = usersId;
        this.pjProjectsId = pjProjectsId;
        this.pjPhotosFolderId = pjPhotosFolderId;
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
    }

    @Generated(hash = 1855765508)
    public AlbumCoverPhoto() {
    }

    public String getPhotoLocation() {
        return this.photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getPhotoName() {
        return this.photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Integer getPjPhotoId() {
        return this.pjPhotoId;
    }

    public void setPjPhotoId(Integer pjPhotoId) {
        this.pjPhotoId = pjPhotoId;
    }

    public Integer getUsersId() {
        return this.usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getPjPhotosFolderId() {
        return this.pjPhotosFolderId;
    }

    public void setPjPhotosFolderId(Integer pjPhotosFolderId) {
        this.pjPhotosFolderId = pjPhotosFolderId;
    }

    public Integer getPjPhotosFolderMobileId() {
        return this.pjPhotosFolderMobileId;
    }

    public void setPjPhotosFolderMobileId(Integer pjPhotosFolderMobileId) {
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
    }


}

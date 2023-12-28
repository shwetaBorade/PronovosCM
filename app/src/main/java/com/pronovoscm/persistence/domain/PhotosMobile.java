package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "Pj_photos_mobile")
public class PhotosMobile {
    @Property(nameInDb = "pj_photos_id")
    Integer pjPhotosId;
//    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "pj_photos_id_mobile")
    Long pjPhotosIdMobile;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "size")
    String size;
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "date_taken")
    Date dateTaken;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "is_aws_sync")
    Boolean isawsSync;
    @Property(nameInDb = "photo_name")
    String photoName;
    @Property(nameInDb = "descriptions")
    String descriptions;
    @Property(nameInDb = "photo_location")
    String photoLocation;
    @Property(nameInDb = "photo_thumb")
    String photoThumb;
    @Property(nameInDb = "uploaded_by")
    String uploadedBy;
    @Property(nameInDb = "pj_photos_folder_id")
    Integer pjPhotosFolderId;
    @Property(nameInDb = "pj_photos_folder_mobile_id")
    Long pjPhotosFolderMobileId;
    @Property(nameInDb = "user_id")
    Integer userId;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "is_in_process")
    Boolean isInProcess;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;


    @Generated(hash = 518242508)
    public PhotosMobile() {
    }

    @Generated(hash = 1397096202)
    public PhotosMobile(Integer pjPhotosId, Long pjPhotosIdMobile,
                        Integer pjProjectsId, String size, Date createdAt, Date dateTaken,
                        Boolean isSync, Boolean isawsSync, String photoName,
                        String descriptions, String photoLocation, String photoThumb,
                        String uploadedBy, Integer pjPhotosFolderId,
                        Long pjPhotosFolderMobileId, Integer userId, Date updatedAt,
                        Boolean isInProcess, Date deletedAt) {
        this.pjPhotosId = pjPhotosId;
        this.pjPhotosIdMobile = pjPhotosIdMobile;
        this.pjProjectsId = pjProjectsId;
        this.size = size;
        this.createdAt = createdAt;
        this.dateTaken = dateTaken;
        this.isSync = isSync;
        this.isawsSync = isawsSync;
        this.photoName = photoName;
        this.descriptions = descriptions;
        this.photoLocation = photoLocation;
        this.photoThumb = photoThumb;
        this.uploadedBy = uploadedBy;
        this.pjPhotosFolderId = pjPhotosFolderId;
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
        this.userId = userId;
        this.updatedAt = updatedAt;
        this.isInProcess = isInProcess;
        this.deletedAt = deletedAt;
    }

    public Integer getPjPhotosId() {
        return this.pjPhotosId;
    }

    public void setPjPhotosId(Integer pjPhotosId) {
        this.pjPhotosId = pjPhotosId;
    }

    public Long getPjPhotosIdMobile() {
        return this.pjPhotosIdMobile;
    }

    public void setPjPhotosIdMobile(Long pjPhotosIdMobile) {
        this.pjPhotosIdMobile = pjPhotosIdMobile;
    }

    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDateTaken() {
        return this.dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Boolean getIsawsSync() {
        return this.isawsSync;
    }

    public void setIsawsSync(Boolean isawsSync) {
        this.isawsSync = isawsSync;
    }

    public String getPhotoName() {
        return this.photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getPhotoLocation() {
        return this.photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getPhotoThumb() {
        return this.photoThumb;
    }

    public void setPhotoThumb(String photoThumb) {
        this.photoThumb = photoThumb;
    }

    public String getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Integer getPjPhotosFolderId() {
        return this.pjPhotosFolderId;
    }

    public void setPjPhotosFolderId(Integer pjPhotosFolderId) {
        this.pjPhotosFolderId = pjPhotosFolderId;
    }

    public Long getPjPhotosFolderMobileId() {
        return this.pjPhotosFolderMobileId;
    }

    public void setPjPhotosFolderMobileId(Long pjPhotosFolderMobileId) {
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsInProcess() {
        return this.isInProcess;
    }

    public void setIsInProcess(Boolean isInProcess) {
        this.isInProcess = isInProcess;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}

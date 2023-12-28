package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
@Entity(nameInDb = "Pj_submittal_attachments")
public class PjSubmittalAttachments {

    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "attach_path")
    public String attachPath;
    @Property(nameInDb = "attachments_id")
    public Integer attachmentsId;
    @Property(nameInDb = "attachments_id_mobile")
    public Integer attachmentsIdMobile;
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @Property(nameInDb = "is_aws_sync")
    public Boolean isAwsSync;
    @Property(nameInDb = "original_name")
    public String originalName;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "pj_submittal_mobile_id")
    public Integer pjSubmittalMobileId;
    @Property(nameInDb = "pj_submittals_id")
    public Integer pjSubmittalsId;
    @Property(nameInDb = "sync_status")
    public Integer syncStatus;
    @Property(nameInDb = "type")
    public String type;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "users_id")
    public Integer usersId;

    @Generated(hash = 1093028319)
    public PjSubmittalAttachments(Long id, String attachPath, Integer attachmentsId,
            Integer attachmentsIdMobile, Date createdAt, Date deletedAt,
            Boolean isAwsSync, String originalName, Integer pjProjectsId,
            Integer pjSubmittalMobileId, Integer pjSubmittalsId, Integer syncStatus,
            String type, Date updatedAt, Integer usersId) {
        this.id = id;
        this.attachPath = attachPath;
        this.attachmentsId = attachmentsId;
        this.attachmentsIdMobile = attachmentsIdMobile;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.isAwsSync = isAwsSync;
        this.originalName = originalName;
        this.pjProjectsId = pjProjectsId;
        this.pjSubmittalMobileId = pjSubmittalMobileId;
        this.pjSubmittalsId = pjSubmittalsId;
        this.syncStatus = syncStatus;
        this.type = type;
        this.updatedAt = updatedAt;
        this.usersId = usersId;
    }

    @Generated(hash = 770791920)
    public PjSubmittalAttachments() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public Integer getAttachmentsId() {
        return attachmentsId;
    }

    public void setAttachmentsId(Integer attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public Integer getAttachmentsIdMobile() {
        return attachmentsIdMobile;
    }

    public void setAttachmentsIdMobile(Integer attachmentsIdMobile) {
        this.attachmentsIdMobile = attachmentsIdMobile;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getAwsSync() {
        return isAwsSync;
    }

    public void setAwsSync(Boolean awsSync) {
        isAwsSync = awsSync;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getPjSubmittalMobileId() {
        return pjSubmittalMobileId;
    }

    public void setPjSubmittalMobileId(Integer pjSubmittalMobileId) {
        this.pjSubmittalMobileId = pjSubmittalMobileId;
    }

    public Integer getPjSubmittalsId() {
        return pjSubmittalsId;
    }

    public void setPjSubmittalsId(Integer pjSubmittalsId) {
        this.pjSubmittalsId = pjSubmittalsId;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public Boolean getIsAwsSync() {
        return this.isAwsSync;
    }

    public void setIsAwsSync(Boolean isAwsSync) {
        this.isAwsSync = isAwsSync;
    }
}

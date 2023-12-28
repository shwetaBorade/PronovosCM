package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_rfi_attachments")
public class PjRfiAttachments {

    @Id(autoincrement = true)
    public Long id;
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;

    @Property(nameInDb = "pj_rfi_attachments_id")
    public Integer pjRfiAttachmentsId;
    @Property(nameInDb = "pj_rfi_id")
    public Integer pjRfiId;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "attach_path")
    public String attachPath;
    @Property(nameInDb = "pj_rfi_replies_id")
    public Integer pjRfiRepliesId;
    @Property(nameInDb = "pj_rfi_origname")
    public String pjRfiOrigName;
    @Property(nameInDb = "type")
    public String type;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @Property(nameInDb = "file_status")
    Integer fileStatus;
    @Property(nameInDb = "is_sync")
    Boolean isSync;

    @Generated(hash = 2145510021)
    public PjRfiAttachments(Long id, Date createdAt, Date updatedAt,
                            Integer pjRfiAttachmentsId, Integer pjRfiId, Integer pjProjectsId,
                            String attachPath, Integer pjRfiRepliesId, String pjRfiOrigName, String type,
                            Date deletedAt, Integer fileStatus, Boolean isSync) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pjRfiAttachmentsId = pjRfiAttachmentsId;
        this.pjRfiId = pjRfiId;
        this.pjProjectsId = pjProjectsId;
        this.attachPath = attachPath;
        this.pjRfiRepliesId = pjRfiRepliesId;
        this.pjRfiOrigName = pjRfiOrigName;
        this.type = type;
        this.deletedAt = deletedAt;
        this.fileStatus = fileStatus;
        this.isSync = isSync;
    }

    @Generated(hash = 1776684322)
    public PjRfiAttachments() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getPjRfiAttachmentsId() {
        return this.pjRfiAttachmentsId;
    }

    public void setPjRfiAttachmentsId(Integer pjRfiAttachmentsId) {
        this.pjRfiAttachmentsId = pjRfiAttachmentsId;
    }

    public Integer getPjRfiId() {
        return this.pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getAttachPath() {
        return this.attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public Integer getPjRfiRepliesId() {
        return this.pjRfiRepliesId;
    }

    public void setPjRfiRepliesId(Integer pjRfiRepliesId) {
        this.pjRfiRepliesId = pjRfiRepliesId;
    }

    public String getPjRfiOrigName() {
        return this.pjRfiOrigName;
    }

    public void setPjRfiOrigName(String pjRfiOrigName) {
        this.pjRfiOrigName = pjRfiOrigName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}


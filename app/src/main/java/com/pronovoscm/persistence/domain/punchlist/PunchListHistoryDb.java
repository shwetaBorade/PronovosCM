package com.pronovoscm.persistence.domain.punchlist;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "PunchListHistory")
public class PunchListHistoryDb {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "users_id")
    private Integer userId;
    @Property(nameInDb = "pj_projects_id")
    private Integer pjProjectsId;
    @Property(nameInDb = "punch_list_audits_id")
    private Integer punchListAuditsId;
    @Property(nameInDb = "punch_list_audits_mobile_id")
    private Integer punchListAuditsMobileId;
    @Property(nameInDb = "punch_lists_id")
    private Integer punchListId;

    @Property(nameInDb = "punch_lists_mobile_id")
    private Integer punchListMobileId;
    @Property(nameInDb = "comments")
    private String comments;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "created_by")
    private String createdBy;

    @Property(nameInDb = "status")
    private Integer status;
    @Property(nameInDb = "created_at")
    private Date createdAt;
    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @Property(nameInDb = "is_attachment_sync")
    Boolean isAttachmentSync;
    @Property(nameInDb = "is_in_progress")
    Boolean isInProgress;
    @Property(nameInDb = "is_sync")
    private Boolean isSync;

    @Property(nameInDb = "created_timestamp")
    private Long createdTimestamp;

    @Property(nameInDb = "created_by_name")
    private String createdByName;

    @Generated(hash = 496964722)
    public PunchListHistoryDb(Long id, Integer userId, Integer pjProjectsId,
            Integer punchListAuditsId, Integer punchListAuditsMobileId,
            Integer punchListId, Integer punchListMobileId, String comments,
            Date deletedAt, String createdBy, Integer status, Date createdAt,
            Date updatedAt, Boolean isAttachmentSync, Boolean isInProgress,
            Boolean isSync, Long createdTimestamp, String createdByName) {
        this.id = id;
        this.userId = userId;
        this.pjProjectsId = pjProjectsId;
        this.punchListAuditsId = punchListAuditsId;
        this.punchListAuditsMobileId = punchListAuditsMobileId;
        this.punchListId = punchListId;
        this.punchListMobileId = punchListMobileId;
        this.comments = comments;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAttachmentSync = isAttachmentSync;
        this.isInProgress = isInProgress;
        this.isSync = isSync;
        this.createdTimestamp = createdTimestamp;
        this.createdByName = createdByName;
    }

    @Generated(hash = 1121717520)
    public PunchListHistoryDb() {
    }

    public Integer getPunchListAuditsId() {
        return punchListAuditsId;
    }

    public void setPunchListAuditsId(Integer punchListAuditsId) {
        this.punchListAuditsId = punchListAuditsId;
    }

    public Integer getPunchListAuditsMobileId() {
        return punchListAuditsMobileId;
    }

    public void setPunchListAuditsMobileId(Integer punchListAuditsMobileId) {
        this.punchListAuditsMobileId = punchListAuditsMobileId;
    }

    public Integer getPunchListId() {
        return punchListId;
    }

    public void setPunchListId(Integer punchListId) {
        this.punchListId = punchListId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAttachmentSync() {
        return isAttachmentSync;
    }

    public void setAttachmentSync(Boolean attachmentSync) {
        isAttachmentSync = attachmentSync;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsAttachmentSync() {
        return this.isAttachmentSync;
    }

    public void setIsAttachmentSync(Boolean isAttachmentSync) {
        this.isAttachmentSync = isAttachmentSync;
    }

    public Boolean getIsInProgress() {
        return this.isInProgress;
    }

    public void setIsInProgress(Boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Integer getPunchListMobileId() {
        return punchListMobileId;
    }

    public void setPunchListMobileId(Integer punchListMobileId) {
        this.punchListMobileId = punchListMobileId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}

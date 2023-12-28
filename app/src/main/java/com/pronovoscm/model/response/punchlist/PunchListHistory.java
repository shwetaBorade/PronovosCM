package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Property;

import java.util.List;

public class PunchListHistory {

    @Property(nameInDb = "users_id")
    private Integer userId;
    @SerializedName("punch_list_audits_id")
    private int punchListAuditsId;
    @SerializedName("punch_list_audits_mobile_id")
    private int punchListAuditsMobileId;
    @SerializedName("punch_lists_id")
    private int punchListId;
    @SerializedName("punch_lists_mobile_id")
    private int punchListMobileId;
    @SerializedName("comments")
    private String comments;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("created_at")
    private String createdAt;
    @Property(nameInDb = "is_sync")
    private Boolean isSync;
    @Property(nameInDb = "is_in_progress")
    Boolean isInProgress;
    @SerializedName("status")
    private int status;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("attachments")
    private List<Attachment> attachments;

    @SerializedName("created_timestamp")
    private Long createdTimestamp;

    @SerializedName("created_by_name")
    private String createdByName;

    public int getPunchListAuditsId() {
        return punchListAuditsId;
    }

    public void setPunchListAuditsId(int punchListAuditsId) {
        this.punchListAuditsId = punchListAuditsId;
    }

    public int getPunchListAuditsMobileId() {
        return punchListAuditsMobileId;
    }

    public void setPunchListAuditsMobileId(int punchListAuditsMobileId) {
        this.punchListAuditsMobileId = punchListAuditsMobileId;
    }

    public int getPunchListId() {
        return punchListId;
    }

    public void setPunchListId(int punchListId) {
        this.punchListId = punchListId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getPunchListMobileId() {
        return punchListMobileId;
    }

    public void setPunchListMobileId(int punchListMobileId) {
        this.punchListMobileId = punchListMobileId;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}


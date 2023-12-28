package com.pronovoscm.persistence.domain.punchlist;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "PunchlistRejectReasonAttachment")
public class PunchListRejectReasonAttachments {
    @Property(nameInDb = "attach_path")
    String attachmentPath;
    @Property(nameInDb = "attachments_id")
    Integer rejectAttachmentId;

    @Property(nameInDb = "pj_projects_id")
    private Integer pjProjectsId;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "attachments_id_mobile")
    Long rejectAttachmentIdMobile;
    @Property(nameInDb = "is_aws_sync")
    Boolean isAwsSync;
    @Property(nameInDb = "users_id")
    Integer usersId;

    @Property(nameInDb = "punch_list_audits_id")
    Integer punchListAuditsId;

    @Property(nameInDb = "punch_list_mobile_id")
    Integer punchListMobileId;
    @Property(nameInDb = "punch_list_audits_mobile_id")
    Long punchListAuditsIdMobile;
    @Property(nameInDb = "punch_list_id")
    Integer punchListId;
    @Property(nameInDb = "type")
    String type = "JPEG";
    @Property(nameInDb = "file_status")
    Integer fileStatus;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "original_name")
    String originalName;

    @Generated(hash = 55202253)
    public PunchListRejectReasonAttachments() {
    }

    @Generated(hash = 597397588)
    public PunchListRejectReasonAttachments(String attachmentPath, Integer rejectAttachmentId, Integer pjProjectsId,
            Long rejectAttachmentIdMobile, Boolean isAwsSync, Integer usersId, Integer punchListAuditsId, Integer punchListMobileId,
            Long punchListAuditsIdMobile, Integer punchListId, String type, Integer fileStatus, Date deletedAt, String originalName) {
        this.attachmentPath = attachmentPath;
        this.rejectAttachmentId = rejectAttachmentId;
        this.pjProjectsId = pjProjectsId;
        this.rejectAttachmentIdMobile = rejectAttachmentIdMobile;
        this.isAwsSync = isAwsSync;
        this.usersId = usersId;
        this.punchListAuditsId = punchListAuditsId;
        this.punchListMobileId = punchListMobileId;
        this.punchListAuditsIdMobile = punchListAuditsIdMobile;
        this.punchListId = punchListId;
        this.type = type;
        this.fileStatus = fileStatus;
        this.deletedAt = deletedAt;
        this.originalName = originalName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getAttachmentPath() {
        return this.attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public Boolean getIsAwsSync() {
        return this.isAwsSync;
    }
    public void setIsAwsSync(Boolean isAwsSync) {
        this.isAwsSync = isAwsSync;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getPunchListId() {
        return this.punchListId;
    }
    public void setPunchListId(Integer punchListId) {
        this.punchListId = punchListId;
    }

    public Integer getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Integer getRejectAttachmentId() {
        return rejectAttachmentId;
    }

    public void setRejectAttachmentId(Integer rejectAttachmentId) {
        this.rejectAttachmentId = rejectAttachmentId;
    }

    public Long getRejectAttachmentIdMobile() {
        return rejectAttachmentIdMobile;
    }

    public void setRejectAttachmentIdMobile(Long rejectAttachmentIdMobile) {
        this.rejectAttachmentIdMobile = rejectAttachmentIdMobile;
    }

    public Boolean getAwsSync() {
        return isAwsSync;
    }

    public void setAwsSync(Boolean awsSync) {
        isAwsSync = awsSync;
    }

    public Integer getPunchListAuditsId() {
        return punchListAuditsId;
    }

    public void setPunchListAuditsId(Integer punchListAuditsId) {
        this.punchListAuditsId = punchListAuditsId;
    }

    public Long getPunchListAuditsIdMobile() {
        return punchListAuditsIdMobile;
    }

    public void setPunchListAuditsIdMobile(Long punchListAuditsIdMobile) {
        this.punchListAuditsIdMobile = punchListAuditsIdMobile;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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

    public Integer getPunchListMobileId() {
        return punchListMobileId;
    }

    public void setPunchListMobileId(Integer punchListMobileId) {
        this.punchListMobileId = punchListMobileId;
    }
}

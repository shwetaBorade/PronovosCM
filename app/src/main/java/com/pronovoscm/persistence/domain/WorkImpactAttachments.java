package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WorkImpactAttachments")
public class WorkImpactAttachments {


    @Property(nameInDb = "attach_path")
    String attachmentPath;
    @Property(nameInDb = "attachment_id")
    Integer attachmentId;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "attachment_id_mobile")
    Long attachmentIdMobile;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "is_aws_sync")
    Boolean isAwsSync;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "work_impact_report_id_mobile")
    Long workImpactReportIdMobile;
    @Property(nameInDb = "work_impact_report_id")
    Integer workImpactReportId;
    @Property(nameInDb = "type")
    String type = "JPEG";
    @Property(nameInDb = "file_status")
    Integer fileStatus;


    @Generated(hash = 1768146800)
    public WorkImpactAttachments(String attachmentPath, Integer attachmentId,
                                 Long attachmentIdMobile, Date deletedAt, Boolean isAwsSync,
                                 Integer usersId, Long workImpactReportIdMobile,
                                 Integer workImpactReportId, String type, Integer fileStatus) {
        this.attachmentPath = attachmentPath;
        this.attachmentId = attachmentId;
        this.attachmentIdMobile = attachmentIdMobile;
        this.deletedAt = deletedAt;
        this.isAwsSync = isAwsSync;
        this.usersId = usersId;
        this.workImpactReportIdMobile = workImpactReportIdMobile;
        this.workImpactReportId = workImpactReportId;
        this.type = type;
        this.fileStatus = fileStatus;
    }

    @Generated(hash = 1187637300)
    public WorkImpactAttachments() {
    }

    public WorkImpactAttachments(WorkImpactAttachments imageTag) {
        this.attachmentPath = imageTag.getAttachmentPath();
        this.attachmentId = imageTag.getAttachmentId();
        this.attachmentIdMobile = imageTag.getAttachmentIdMobile();
        this.deletedAt = imageTag.getDeletedAt();
        this.isAwsSync = imageTag.getIsAwsSync();
        this.usersId = imageTag.getUsersId();
        this.workImpactReportIdMobile = imageTag.getWorkImpactReportIdMobile();
        this.workImpactReportId = imageTag.getWorkImpactReportId();
        this.type = imageTag.getType();
        this.fileStatus = imageTag.getFileStatus();
    }


    public Boolean getAwsSync() {
        return isAwsSync;
    }

    public void setAwsSync(Boolean awsSync) {
        isAwsSync = awsSync;
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

    public Integer getAttachmentId() {
        return this.attachmentId;
    }
    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }
    public Long getAttachmentIdMobile() {
        return this.attachmentIdMobile;
    }
    public void setAttachmentIdMobile(Long attachmentIdMobile) {
        this.attachmentIdMobile = attachmentIdMobile;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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
    public Long getWorkImpactReportIdMobile() {
        return this.workImpactReportIdMobile;
    }

    public void setWorkImpactReportIdMobile(Long workImpactReportIdMobile) {
        this.workImpactReportIdMobile = workImpactReportIdMobile;
    }

    public Integer getWorkImpactReportId() {
        return this.workImpactReportId;
    }

    public void setWorkImpactReportId(Integer workImpactReportId) {
        this.workImpactReportId = workImpactReportId;
    }

    @Override
    public String toString() {
        return "WorkImpactAttachments{" +
                "attachmentId=" + attachmentId +
                ", attachmentIdMobile=" + attachmentIdMobile +
                ", deletedAt=" + deletedAt +
                ", isAwsSync=" + isAwsSync +
                " path  = " + attachmentPath +
                '}';
    }

    public Integer getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }
}

package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WorkDetailsAttachments")
public class WorkDetailsAttachments {


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
    @Property(nameInDb = "work_details_report_id_mobile")
    Long workDetailsReportIdMobile;
    @Property(nameInDb = "work_details_report_id")
    Integer workDetailsReportId;

    @Property(nameInDb = "file_status")
    Integer fileStatus;
    @Property(nameInDb = "type")
    String type = "JPEG";

    @Generated(hash = 75089515)
    public WorkDetailsAttachments() {
    }

    @Generated(hash = 1814785850)
    public WorkDetailsAttachments(String attachmentPath, Integer attachmentId,
                                  Long attachmentIdMobile, Date deletedAt, Boolean isAwsSync,
                                  Integer usersId, Long workDetailsReportIdMobile,
                                  Integer workDetailsReportId, Integer fileStatus, String type) {
        this.attachmentPath = attachmentPath;
        this.attachmentId = attachmentId;
        this.attachmentIdMobile = attachmentIdMobile;
        this.deletedAt = deletedAt;
        this.isAwsSync = isAwsSync;
        this.usersId = usersId;
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
        this.workDetailsReportId = workDetailsReportId;
        this.fileStatus = fileStatus;
        this.type = type;
    }

    public WorkDetailsAttachments(WorkDetailsAttachments workDetails) {
        this.attachmentPath = workDetails.attachmentPath;
        this.attachmentId = workDetails.attachmentId;
        this.attachmentIdMobile = workDetails.attachmentIdMobile;
        this.deletedAt = workDetails.deletedAt;
        this.isAwsSync = workDetails.isAwsSync;
        this.usersId = workDetails.usersId;
        this.workDetailsReportIdMobile = workDetails.workDetailsReportIdMobile;
        this.workDetailsReportId = workDetails.workDetailsReportId;
        this.fileStatus = workDetails.fileStatus;
        this.type = workDetails.type;
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
    public Long getWorkDetailsReportIdMobile() {
        return this.workDetailsReportIdMobile;
    }

    public void setWorkDetailsReportIdMobile(Long workDetailsReportIdMobile) {
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
    }

    public Integer getWorkDetailsReportId() {
        return this.workDetailsReportId;
    }

    public void setWorkDetailsReportId(Integer workDetailsReportId) {
        this.workDetailsReportId = workDetailsReportId;
    }

    public Integer getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

}

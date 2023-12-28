package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "PunchlistAttachment")
public class PunchListAttachments {
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
    @Property(nameInDb = "punch_list_id_mobile")
    Long punchListIdMobile;
    @Property(nameInDb = "punch_list_id")
    Integer punchListId;
    @Property(nameInDb = "type")
    String type = "JPEG";
    @Property(nameInDb = "file_status")
    Integer fileStatus;

    @Generated(hash = 344829260)
    public PunchListAttachments() {
    }

    @Generated(hash = 534629984)
    public PunchListAttachments(String attachmentPath, Integer attachmentId,
                                Long attachmentIdMobile, Date deletedAt, Boolean isAwsSync,
                                Integer usersId, Long punchListIdMobile, Integer punchListId,
                                String type, Integer fileStatus) {
        this.attachmentPath = attachmentPath;
        this.attachmentId = attachmentId;
        this.attachmentIdMobile = attachmentIdMobile;
        this.deletedAt = deletedAt;
        this.isAwsSync = isAwsSync;
        this.usersId = usersId;
        this.punchListIdMobile = punchListIdMobile;
        this.punchListId = punchListId;
        this.type = type;
        this.fileStatus = fileStatus;
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
    public Long getPunchListIdMobile() {
        return this.punchListIdMobile;
    }

    public void setPunchListIdMobile(Long punchListIdMobile) {
        this.punchListIdMobile = punchListIdMobile;
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


}

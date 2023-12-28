package com.pronovoscm.persistence.domain;

import com.pronovoscm.utils.database.GreenConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created on 5/11/18.
 *
 * @author GWL
 */
@Entity(nameInDb = "punchlist")
public class PunchlistDb {
    @Property(nameInDb = "users_id")
    private Integer userId;
    @Property(nameInDb = "assigned_to")
    @Convert(converter = GreenConverter.class, columnType = String.class)
    private List<String> assignedTo;
    @Property(nameInDb = "assigned_cc")
    @Convert(converter = GreenConverter.class, columnType = String.class)
    private List<String> assignedCcList;
    @Property(nameInDb = "assignee_name")
    @Convert(converter = GreenConverter.class, columnType = String.class)
    private List<String> assigneeName;
    @Property(nameInDb = "descriptions")
    private String descriptions;
    @Property(nameInDb = "is_sync")
    private Boolean isSync;
    @Property(nameInDb = "item_number")
    private Integer itemNumber;
    @Property(nameInDb = "location")
    private String location;
    @Property(nameInDb = "pj_projects_id")
    private Integer pjProjectsId;
    @Property(nameInDb = "punch_list_id")
    private Integer punchlistId;
    @Index(unique = true)
    @Property(nameInDb = "punch_list_id_mobile")
    private Long punchlistIdMobile;
    @Property(nameInDb = "status")
    private Integer status;
    @Property(nameInDb = "date_created")
    private Date dateCreated;
    @Property(nameInDb = "date_due")
    private Date dateDue;
    @Property(nameInDb = "created_by")
    private String createdBy;
    @Property(nameInDb = "created_by_userid")
    private String createdByUserId;
    @Property(nameInDb = "created_at")
    private Date createdAt;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "is_attachment_sync")
    Boolean isAttachmentSync;
    @Property(nameInDb = "is_in_progress")
    Boolean isInProgress;
    @Property(nameInDb = "send_email")
    private Integer sendEmail;
    @Property(nameInDb = "comments")
    private String comments = "";


    @Generated(hash = 2098453483)
    public PunchlistDb(Integer userId, List<String> assignedTo, List<String> assignedCcList, List<String> assigneeName,
            String descriptions, Boolean isSync, Integer itemNumber, String location, Integer pjProjectsId, Integer punchlistId,
            Long punchlistIdMobile, Integer status, Date dateCreated, Date dateDue, String createdBy, String createdByUserId,
            Date createdAt, Date deletedAt, Boolean isAttachmentSync, Boolean isInProgress, Integer sendEmail, String comments) {
        this.userId = userId;
        this.assignedTo = assignedTo;
        this.assignedCcList = assignedCcList;
        this.assigneeName = assigneeName;
        this.descriptions = descriptions;
        this.isSync = isSync;
        this.itemNumber = itemNumber;
        this.location = location;
        this.pjProjectsId = pjProjectsId;
        this.punchlistId = punchlistId;
        this.punchlistIdMobile = punchlistIdMobile;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
        this.createdBy = createdBy;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.isAttachmentSync = isAttachmentSync;
        this.isInProgress = isInProgress;
        this.sendEmail = sendEmail;
        this.comments = comments;
    }


    @Generated(hash = 1061380544)
    public PunchlistDb() {
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<String> getAssigneeName() {
        return this.assigneeName;
    }

    public void setAssigneeName(List<String> assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getDescriptions() {
        return this.descriptions;
    }


    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }


    public Boolean getIsSync() {
        return this.isSync;
    }


    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }


    public Integer getItemNumber() {
        return this.itemNumber;
    }


    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }


    public String getLocation() {
        return this.location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }


    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }


    public Integer getPunchlistId() {
        return this.punchlistId;
    }


    public void setPunchlistId(Integer punchlistId) {
        this.punchlistId = punchlistId;
    }


    public Long getPunchlistIdMobile() {
        return this.punchlistIdMobile;
    }


    public void setPunchlistIdMobile(Long punchlistIdMobile) {
        this.punchlistIdMobile = punchlistIdMobile;
    }


    public Integer getStatus() {
        return this.status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public Date getDateCreated() {
        return this.dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    public Date getDateDue() {
        return this.dateDue;
    }


    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }


    public String getCreatedBy() {
        return this.createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getCreatedByUserId() {
        return this.createdByUserId;
    }


    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }


    public Date getCreatedAt() {
        return this.createdAt;
    }


    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public Date getDeletedAt() {
        return this.deletedAt;
    }


    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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


    public Integer getSendEmail() {
        return this.sendEmail;
    }


    public void setSendEmail(Integer sendEmail) {
        this.sendEmail = sendEmail;
    }

    public List<String> getAssignedTo() { return this.assignedTo;}

    public void setAssignedTo(List<String> assignedTo) { this.assignedTo = assignedTo;}

    public List<String> getAssignedCcList() {
        return assignedCcList;
    }

    public void setAssignedCcList(List<String> assignedCcList) {
        this.assignedCcList = assignedCcList;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

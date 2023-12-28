package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

@Entity(nameInDb = "pj_rfi")
public class PjRfi implements Serializable {

    public static final long serialVersionUID = 592999878;
    @Id(autoincrement = true)
    public Long id;
    @Property(nameInDb = "date_submitted")
    public Date dateSubmitted;
    @Property(nameInDb = "received_date")
    public Date receivedDate;
    @Property(nameInDb = "due_date")
    public Date dueDate;
    @Property(nameInDb = "date_sent")
    public Date dateSent;
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @Property(nameInDb = "created_by")
    public Integer createdBy;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "updated_by")
    public Integer updatedBy;
    @Property(nameInDb = "tenant_id")
    public Integer tenantId;
    @Property(nameInDb = "pj_rfi_id")
    public Integer pjRfiId;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "rfi_no")
    public String rfiNumber;
    @Property(nameInDb = "orig_rfi_no")
    public String origRfiNumber;
    @Property(nameInDb = "rfi_title")
    public String rfiTitle;
    @Property(nameInDb = "status")
    public Integer status;
    @Property(nameInDb = "internal_author")
    public Integer internalAuthor;
    @Property(nameInDb = "received_from")
    public String receivedFrom;
    @Property(nameInDb = "response_days")
    public Integer responseDays;
    @Property(nameInDb = "assigned_to")
    public Integer assignedTo;
    @Property(nameInDb = "ref_drawing_number")
    public String refDrawingNumber;
    @Property(nameInDb = "location")
    public String location;
    @Property(nameInDb = "schedule_impact_days")
    public Integer scheduleImpactDays;
    @Property(nameInDb = "question")
    public String question;
    @Property(nameInDb = "cc")
    public Integer cc;
    @Property(nameInDb = "ref_specification")
    public String refSpecification;
    @Property(nameInDb = "cost_impact")
    public Integer costImpact;
    @Property(nameInDb = "is_rfi_sent")
    public Integer isRfiSent;
    @Property(nameInDb = "attachment")
    public String attachment;

    @Property(nameInDb = "author_name")
    public String authorName;

    @Property(nameInDb = "receiver_name")
    public String receiverName;


    @Generated(hash = 1502051926)
    public PjRfi(Long id, Date dateSubmitted, Date receivedDate, Date dueDate,
                 Date dateSent, Date createdAt, Date deletedAt, Integer createdBy,
                 Date updatedAt, Integer updatedBy, Integer tenantId, Integer pjRfiId,
                 Integer pjProjectsId, String rfiNumber, String origRfiNumber,
                 String rfiTitle, Integer status, Integer internalAuthor,
                 String receivedFrom, Integer responseDays, Integer assignedTo,
                 String refDrawingNumber, String location, Integer scheduleImpactDays,
                 String question, Integer cc, String refSpecification,
                 Integer costImpact, Integer isRfiSent, String attachment,
                 String authorName, String receiverName) {
        this.id = id;
        this.dateSubmitted = dateSubmitted;
        this.receivedDate = receivedDate;
        this.dueDate = dueDate;
        this.dateSent = dateSent;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.tenantId = tenantId;
        this.pjRfiId = pjRfiId;
        this.pjProjectsId = pjProjectsId;
        this.rfiNumber = rfiNumber;
        this.origRfiNumber = origRfiNumber;
        this.rfiTitle = rfiTitle;
        this.status = status;
        this.internalAuthor = internalAuthor;
        this.receivedFrom = receivedFrom;
        this.responseDays = responseDays;
        this.assignedTo = assignedTo;
        this.refDrawingNumber = refDrawingNumber;
        this.location = location;
        this.scheduleImpactDays = scheduleImpactDays;
        this.question = question;
        this.cc = cc;
        this.refSpecification = refSpecification;
        this.costImpact = costImpact;
        this.isRfiSent = isRfiSent;
        this.attachment = attachment;
        this.authorName = authorName;
        this.receiverName = receiverName;
    }

    @Generated(hash = 561350854)
    public PjRfi() {
    }



    public Date getDateSubmitted() {
        return this.dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Date getReceivedDate() {
        return this.receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDateSent() {
        return this.dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
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

    public String getRfiNumber() {
        return this.rfiNumber;
    }

    public void setRfiNumber(String rfiNumber) {
        this.rfiNumber = rfiNumber;
    }

    public String getOrigRfiNumber() {
        return this.origRfiNumber;
    }

    public void setOrigRfiNumber(String origRfiNumber) {
        this.origRfiNumber = origRfiNumber;
    }

    public String getRfiTitle() {
        return this.rfiTitle;
    }

    public void setRfiTitle(String rfiTitle) {
        this.rfiTitle = rfiTitle;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInternalAuthor() {
        return this.internalAuthor;
    }

    public void setInternalAuthor(Integer internalAuthor) {
        this.internalAuthor = internalAuthor;
    }

    public String getReceivedFrom() {
        return this.receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public Integer getResponseDays() {
        return this.responseDays;
    }

    public void setResponseDays(Integer responseDays) {
        this.responseDays = responseDays;
    }

    public Integer getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRefDrawingNumber() {
        return this.refDrawingNumber;
    }

    public void setRefDrawingNumber(String refDrawingNumber) {
        this.refDrawingNumber = refDrawingNumber;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getScheduleImpactDays() {
        return this.scheduleImpactDays;
    }

    public void setScheduleImpactDays(Integer scheduleImpactDays) {
        this.scheduleImpactDays = scheduleImpactDays;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getCc() {
        return this.cc;
    }

    public void setCc(Integer cc) {
        this.cc = cc;
    }

    public String getRefSpecification() {
        return this.refSpecification;
    }

    public void setRefSpecification(String refSpecification) {
        this.refSpecification = refSpecification;
    }

    public Integer getCostImpact() {
        return this.costImpact;
    }

    public void setCostImpact(Integer costImpact) {
        this.costImpact = costImpact;
    }

    public Integer getIsRfiSent() {
        return this.isRfiSent;
    }

    public void setIsRfiSent(Integer isRfiSent) {
        this.isRfiSent = isRfiSent;
    }

    public String getAttachment() {
        return this.attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


}

package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Pj_submittal")
public class PjSubmittals implements Serializable {
    public static final long serialVersionUID = 536871008;
    @Id(autoincrement = true)
    public Long id;
    @Property(nameInDb = "author")
    public String author;
    @Property(nameInDb = "submittal_author_name")
    public String submittalAuthorName;
    @Property(nameInDb = "ball_in_court")
    public String ballInCourt;
    @Property(nameInDb = "closed_date")
    public Date closedDate;
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "current_response_status")
    public Integer currentResponseStatus;
    @Property(nameInDb = "date_sent")
    public Date dateSent;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @Property(nameInDb = "due_date")
    public Date dueDate;
    @Property(nameInDb = "detail_updated_at")
    public Date detailUpdatedAt;
    @Property(nameInDb = "is_in_process")
    public Boolean is_in_process;
    @Property(nameInDb = "is_submittal_sent")
    public Integer isSubmittalSent;
    @Property(nameInDb = "is_sync")
    public Boolean isSync;
    @Property(nameInDb = "is_detailed_sync")
    public Boolean isDetailedSync;
    @Property(nameInDb = "lead_time")
    public String leadTime;
    @Property(nameInDb = "location")
    public String location;
    @Property(nameInDb = "onsite_date")
    public Date onsiteDate;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "pj_submittal_mobile_id")
    public Integer pjSubmittalMobileId;
    @Property(nameInDb = "pj_submittals_id")
    public Integer pjSubmittalsId;
    @Property(nameInDb = "received_date")
    public Date receivedDate;
    @Property(nameInDb = "received_from")
    public String receivedFrom;
    @Property(nameInDb = "revision")
    public Integer revision;
    @Property(nameInDb = "spec_section")
    public String specSection;
    @Property(nameInDb = "submittal_number")
    public String submittalNumber;
    @Property(nameInDb = "submittal_title")
    public String submittalTitle;
    @Property(nameInDb = "submittal_type")
    public String submittalType;
    @Property(nameInDb = "description")
    public String description;
    @Property(nameInDb = "submitted_date")
    public Date submittedDate;
    @Property(nameInDb = "status")
    public String status;
    @Property(nameInDb = "submittal_status")
    public Integer submittalStatus;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "tenant_id")
    public Integer tenantId;
    @Property(nameInDb = "users_id")
    public Integer users_id;



    @Generated(hash = 1286112020)
    public PjSubmittals(Long id, String author, String submittalAuthorName,
            String ballInCourt, Date closedDate, Date createdAt,
            Integer currentResponseStatus, Date dateSent, Date deletedAt,
            Date dueDate, Date detailUpdatedAt, Boolean is_in_process,
            Integer isSubmittalSent, Boolean isSync, Boolean isDetailedSync,
            String leadTime, String location, Date onsiteDate, Integer pjProjectsId,
            Integer pjSubmittalMobileId, Integer pjSubmittalsId, Date receivedDate,
            String receivedFrom, Integer revision, String specSection,
            String submittalNumber, String submittalTitle, String submittalType,
            String description, Date submittedDate, String status,
            Integer submittalStatus, Date updatedAt, Integer tenantId,
            Integer users_id) {
        this.id = id;
        this.author = author;
        this.submittalAuthorName = submittalAuthorName;
        this.ballInCourt = ballInCourt;
        this.closedDate = closedDate;
        this.createdAt = createdAt;
        this.currentResponseStatus = currentResponseStatus;
        this.dateSent = dateSent;
        this.deletedAt = deletedAt;
        this.dueDate = dueDate;
        this.detailUpdatedAt = detailUpdatedAt;
        this.is_in_process = is_in_process;
        this.isSubmittalSent = isSubmittalSent;
        this.isSync = isSync;
        this.isDetailedSync = isDetailedSync;
        this.leadTime = leadTime;
        this.location = location;
        this.onsiteDate = onsiteDate;
        this.pjProjectsId = pjProjectsId;
        this.pjSubmittalMobileId = pjSubmittalMobileId;
        this.pjSubmittalsId = pjSubmittalsId;
        this.receivedDate = receivedDate;
        this.receivedFrom = receivedFrom;
        this.revision = revision;
        this.specSection = specSection;
        this.submittalNumber = submittalNumber;
        this.submittalTitle = submittalTitle;
        this.submittalType = submittalType;
        this.description = description;
        this.submittedDate = submittedDate;
        this.status = status;
        this.submittalStatus = submittalStatus;
        this.updatedAt = updatedAt;
        this.tenantId = tenantId;
        this.users_id = users_id;
    }

    @Generated(hash = 633786951)
    public PjSubmittals() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBallInCourt() {
        return ballInCourt;
    }

    public void setBallInCourt(String ballInCourt) {
        this.ballInCourt = ballInCourt;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCurrentResponseStatus() {
        return currentResponseStatus;
    }

    public void setCurrentResponseStatus(Integer currentResponseStatus) {
        this.currentResponseStatus = currentResponseStatus;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDetailUpdatedAt() {
        return detailUpdatedAt;
    }

    public void setDetailUpdatedAt(Date detailUpdatedAt) {
        this.detailUpdatedAt = detailUpdatedAt;
    }

    public Boolean getIs_in_process() {
        return is_in_process;
    }

    public void setIs_in_process(Boolean is_in_process) {
        this.is_in_process = is_in_process;
    }

    public Integer getIsSubmittalSent() {
        return isSubmittalSent;
    }

    public void setIsSubmittalSent(Integer isSubmittalSent) {
        this.isSubmittalSent = isSubmittalSent;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public Boolean getDetailedSync() {
        return isDetailedSync;
    }

    public void setDetailedSync(Boolean detailedSync) {
        isDetailedSync = detailedSync;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime = leadTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getOnsiteDate() {
        return onsiteDate;
    }

    public void setOnsiteDate(Date onsiteDate) {
        this.onsiteDate = onsiteDate;
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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getSpecSection() {
        return specSection;
    }

    public void setSpecSection(String specSection) {
        this.specSection = specSection;
    }

    public String getSubmittalNumber() {
        return submittalNumber;
    }

    public void setSubmittalNumber(String submittalNumber) {
        this.submittalNumber = submittalNumber;
    }

    public String getSubmittalTitle() {
        return submittalTitle;
    }

    public void setSubmittalTitle(String submittalTitle) {
        this.submittalTitle = submittalTitle;
    }

    public String getSubmittalType() {
        return submittalType;
    }

    public void setSubmittalType(String submittalType) {
        this.submittalType = submittalType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSubmittalStatus() {
        return submittalStatus;
    }

    public void setSubmittalStatus(Integer submittalStatus) {
        this.submittalStatus = submittalStatus;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getUsers_id() {
        return users_id;
    }

    public void setUsers_id(Integer users_id) {
        this.users_id = users_id;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Boolean getIsDetailedSync() {
        return this.isDetailedSync;
    }

    public void setIsDetailedSync(Boolean isDetailedSync) {
        this.isDetailedSync = isDetailedSync;
    }

    public String getSubmittalAuthorName() {
        return submittalAuthorName;
    }

    public void setSubmittalAuthorName(String submittalAuthorName) {
        this.submittalAuthorName = submittalAuthorName;
    }
}

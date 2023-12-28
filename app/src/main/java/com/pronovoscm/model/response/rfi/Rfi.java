package com.pronovoscm.model.response.rfi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rfi {

    @Expose
    @SerializedName("author_name")
    public String authorName;
    @SerializedName("pj_rfi_id")
    @Expose
    private Integer pjRfiId;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;
    @SerializedName("rfi_no")
    @Expose
    private String rfiNo;
    @SerializedName("orig_rfi_no")
    @Expose
    private String origRfiNo;
    @SerializedName("rfi_title")
    @Expose
    private String rfiTitle;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("internal_author")
    @Expose
    private Integer internalAuthor;
    @SerializedName("received_from")
    @Expose
    private String receivedFrom;
    @SerializedName("response_days")
    @Expose
    private Integer responseDays;
    @SerializedName("assigned_to")
    @Expose
    private Integer assignedTo;
    @SerializedName("ref_drawing_number")
    @Expose
    private String refDrawingNumber;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("schedule_impact_days")
    @Expose
    private Integer scheduleImpactDays;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("date_submitted")
    @Expose
    private String dateSubmitted;
    @SerializedName("received_date")
    @Expose
    private String receivedDate;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("date_sent")
    @Expose
    private String dateSent;
    @SerializedName("cc")
    @Expose
    private Integer cc;
    @SerializedName("ref_specification")
    @Expose
    private String refSpecification;
    @SerializedName("cost_impact")
    @Expose
    private Integer costImpact;
    @SerializedName("is_rfi_sent")
    @Expose
    private Integer isRfiSent;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("receiver_name")
    @Expose
    private String receiverName;
    @SerializedName("updated_by")
    @Expose
    private Integer updatedBy;
    @SerializedName("tenant_id")
    @Expose
    private Integer tenantId;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getPjRfiId() {
        return pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getRfiNo() {
        return rfiNo;
    }

    public void setRfiNo(String rfiNo) {
        this.rfiNo = rfiNo;
    }

    public String getOrigRfiNo() {
        return origRfiNo;
    }

    public void setOrigRfiNo(String origRfiNo) {
        this.origRfiNo = origRfiNo;
    }

    public String getRfiTitle() {
        return rfiTitle;
    }

    public void setRfiTitle(String rfiTitle) {
        this.rfiTitle = rfiTitle;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInternalAuthor() {
        return internalAuthor;
    }

    public void setInternalAuthor(Integer internalAuthor) {
        this.internalAuthor = internalAuthor;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public Integer getResponseDays() {
        return responseDays;
    }

    public void setResponseDays(Integer responseDays) {
        this.responseDays = responseDays;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRefDrawingNumber() {
        return refDrawingNumber;
    }

    public void setRefDrawingNumber(String refDrawingNumber) {
        this.refDrawingNumber = refDrawingNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getScheduleImpactDays() {
        return scheduleImpactDays;
    }

    public void setScheduleImpactDays(Integer scheduleImpactDays) {
        this.scheduleImpactDays = scheduleImpactDays;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public Integer getCc() {
        return cc;
    }

    public void setCc(Integer cc) {
        this.cc = cc;
    }

    public String getRefSpecification() {
        return refSpecification;
    }

    public void setRefSpecification(String refSpecification) {
        this.refSpecification = refSpecification;
    }

    public Integer getCostImpact() {
        return costImpact;
    }

    public void setCostImpact(Integer costImpact) {
        this.costImpact = costImpact;
    }

    public Integer getIsRfiSent() {
        return isRfiSent;
    }

    public void setIsRfiSent(Integer isRfiSent) {
        this.isRfiSent = isRfiSent;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

}

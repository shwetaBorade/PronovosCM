package com.pronovoscm.model.response.submittals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Submittals {
    @SerializedName("pj_submittals_id")
    @Expose
    private Integer pjSubmittalsId;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;
    @SerializedName("submittal_number")
    @Expose
    private String submittalNumber;
    @SerializedName("submittal_status")
    @Expose
    private int submittal_status;
    @SerializedName("revision")
    @Expose
    private int revision;
    @SerializedName("submittal_title")
    @Expose
    private String submittalTitle;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("submitted_date")
    @Expose
    private String submittedDate;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("current_response_status")
    @Expose
    private int currentResponseStatus;
    @SerializedName("ball_in_court")
    @Expose
    private String ballInCourt;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("submittal_author_name")
    @Expose
    private String submittalAuthorName;

    public String getSubmittalAuthorName() {
        return submittalAuthorName;
    }

    public void setSubmittalAuthorName(String submittalAuthorName) {
        this.submittalAuthorName = submittalAuthorName;
    }

    @SerializedName("received_from")
    @Expose
    private String receivedFrom;
    @SerializedName("spec_section")
    @Expose
    private String specSection;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("submittal_type")
    @Expose
    private String submittalType;
    @SerializedName("received_date")
    @Expose
    private String receivedDate;
    @SerializedName("onsite_date")
    @Expose
    private String onsiteDate;
    @SerializedName("closed_date")
    @Expose
    private String closedDate;
    @SerializedName("date_sent")
    @Expose
    private String dateSent;
    @SerializedName("lead_time")
    @Expose
    private String leadTime;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_submittal_sent")
    @Expose
    private int isSubmittalSent;
    @SerializedName("tenant_id")
    @Expose
    private int tenant_id;

    @SerializedName("assignee")
    @Expose
    private List<AssigneeSubmittals> assigneeSubmittals = null;
    @SerializedName("cclist")
    @Expose
    private List<CcListSubmittals> ccListSubmittals = null;
    @SerializedName("attachments")
    @Expose
    private List<AttachmentsSubmittals> attachmentsSubmittals = null;
    @SerializedName("previous_revisions")
    @Expose
    private List<Integer> previousRevisionsList = null;

    public List<Integer> getPreviousRevisionsList() {
        return previousRevisionsList;
    }

    public void setPreviousRevisionsList(List<Integer> previousRevisionsList) {
        this.previousRevisionsList = previousRevisionsList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public String getSpecSection() {
        return specSection;
    }

    public void setSpecSection(String specSection) {
        this.specSection = specSection;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubmittalType() {
        return submittalType;
    }

    public void setSubmittalType(String submittalType) {
        this.submittalType = submittalType;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getOnsiteDate() {
        return onsiteDate;
    }

    public void setOnsiteDate(String onsiteDate) {
        this.onsiteDate = onsiteDate;
    }

    public String getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(String closedDate) {
        this.closedDate = closedDate;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime = leadTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsSubmittalSent() {
        return isSubmittalSent;
    }

    public void setIsSubmittalSent(int isSubmittalSent) {
        this.isSubmittalSent = isSubmittalSent;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    public List<CcListSubmittals> getCcListSubmittals() {
        return ccListSubmittals;
    }

    public void setCcListSubmittals(List<CcListSubmittals> ccListSubmittals) {
        this.ccListSubmittals = ccListSubmittals;
    }

    public List<AttachmentsSubmittals> getAttachmentsSubmittals() {
        return attachmentsSubmittals;
    }

    public void setAttachmentsSubmittals(List<AttachmentsSubmittals> attachmentsSubmittals) {
        this.attachmentsSubmittals = attachmentsSubmittals;
    }

    public String getBallInCourt() {
        return ballInCourt;
    }

    public void setBallInCourt(String ballInCourt) {
        this.ballInCourt = ballInCourt;
    }

    public int getCurrentResponseStatus() {
        return currentResponseStatus;
    }

    public void setCurrentResponseStatus(int currentResponseStatus) {
        this.currentResponseStatus = currentResponseStatus;
    }


    public List<AssigneeSubmittals> getAssigneeSubmittals() {
        return assigneeSubmittals;
    }

    public void setAssigneeSubmittals(List<AssigneeSubmittals> assigneeSubmittals) {
        this.assigneeSubmittals = assigneeSubmittals;
    }

    public Integer getPjSubmittalsId() {
        return pjSubmittalsId;
    }

    public void setPjSubmittalsId(Integer pjSubmittalsId) {
        this.pjSubmittalsId = pjSubmittalsId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getSubmittalNumber() {
        return submittalNumber;
    }

    public void setSubmittalNumber(String submittalNumber) {
        this.submittalNumber = submittalNumber;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getSubmittalTitle() {
        return submittalTitle;
    }

    public void setSubmittalTitle(String submittalTitle) {
        this.submittalTitle = submittalTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSubmittal_status() {
        return submittal_status;
    }

    public void setSubmittal_status(int submittal_status) {
        this.submittal_status = submittal_status;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

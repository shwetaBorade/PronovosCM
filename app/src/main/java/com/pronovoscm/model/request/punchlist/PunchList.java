package com.pronovoscm.model.request.punchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PunchList {
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("assignee_name")
    private List<String> assigneeName;
    @SerializedName("punch_lists_id_mobile")
    private String punchListsIdMobile;
    @SerializedName("date_due")
    private String dateDue;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("assigned_to")
    private List<String> assignedTo;
    @SerializedName("assigned_cc")
    private List<String> assignedCc;

    @SerializedName("attachments")
    private List<Attachments> attachments;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("status")
    private String status;
    @SerializedName("date_created")
    private String dateCreated;
    @SerializedName("location")
    private String location;
    @SerializedName("item_number")
    private String itemNumber;
    @SerializedName("pj_projects_id")
    private String pjProjectsId;
    @SerializedName("description")
    private String description;
    @SerializedName("punch_lists_id")
    private String punchListsId;
    @SerializedName("send_email")
    private int sendEmail;

    @SerializedName("comments")
    private String comments;

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<String> getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(List<String> assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getPunchListsIdMobile() {
        return punchListsIdMobile;
    }

    public void setPunchListsIdMobile(String punchListsIdMobile) {
        this.punchListsIdMobile = punchListsIdMobile;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /*public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }*/

    public List<String> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(List<String> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(String pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPunchListsId() {
        return punchListsId;
    }

    public void setPunchListsId(String punchListsId) {
        this.punchListsId = punchListsId;
    }

    public int getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(int sendEmail) {
        this.sendEmail = sendEmail;
    }

    public List<String> getAssignedCc() {
        return assignedCc;
    }

    public void setAssignedCc(List<String> assignedCc) {
        this.assignedCc = assignedCc;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

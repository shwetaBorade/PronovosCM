package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Punchlist {
    @SerializedName("linked_drawings")
    private List<LinkedDrawings> linkedDrawings;
    @SerializedName("date_due")
    private String dateDue;

    @SerializedName("punch_lists_id_mobile")
    private int punchListsIdMobile;

    @SerializedName("attachments")
    private List<Attachment> attachments;

    @SerializedName("pj_projects_id")
    private int pjProjectsId;

    @SerializedName("date_created")
    private String dateCreated;

    @SerializedName("item_number")
    private int itemNumber;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_by_userid")
    private int createdByUserid;

    @SerializedName("deleted_at")
    private String deletedAt;

    @SerializedName("assignee_name")
    private List<String> assigneeName;

    @SerializedName("punch_lists_id")
    private int punchListsId;

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private int status;

    @SerializedName("assigned_to")
    private List<String> assignedTo;
//    private int assignedTo;

    @SerializedName("assigned_cc")
    private List<String> assignedCCs;

    @SerializedName("comments")
    private String comments;



    public List<LinkedDrawings> getLinkedDrawings() {
        return linkedDrawings;
    }

    public void setLinkedDrawings(List<LinkedDrawings> linkedDrawings) {
        this.linkedDrawings = linkedDrawings;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public int getPunchListsIdMobile() {
        return punchListsIdMobile;
    }

    public void setPunchListsIdMobile(int punchListsIdMobile) {
        this.punchListsIdMobile = punchListsIdMobile;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getCreatedByUserid() {
        return createdByUserid;
    }

    public void setCreatedByUserid(int createdByUserid) {
        this.createdByUserid = createdByUserid;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

   /* public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }*/

    public int getPunchListsId() {
        return punchListsId;
    }

    public void setPunchListsId(int punchListsId) {
        this.punchListsId = punchListsId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }*/

    public List<String> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(List<String> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<String> getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(List<String> assigneeName) {
        this.assigneeName = assigneeName;
    }

    public List<String> getAssignedCCs() {
        return assignedCCs;
    }

    public void setAssignedCCs(List<String> assignedCCs) {
        this.assignedCCs = assignedCCs;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
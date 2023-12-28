package com.pronovoscm.model.response.submittals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssigneeSubmittals {
    @SerializedName("pj_submittal_contact_list_id")
    private int pjSubmittalContactListId;
    @SerializedName("pj_submittals_id")
    private int pjSubmittalsId;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("sort_order")
    private int sortOrder;
    @SerializedName("response")
    private int response;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("contact_list")
    private String contactList;
    @SerializedName("response_date")
    private String responseDate;
    @SerializedName("comments")
    private String comments;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("deleted_at")
    private String deletedAt;

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    @SerializedName("pj_submittal_approver_responses_id")
    private int pj_submittal_approver_responses_id;
    @SerializedName("attachments")
    @Expose
    private List<AttachmentsSubmittals> attachmentsSubmittals = null;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactList() {
        return contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public int getPj_submittal_approver_responses_id() {
        return pj_submittal_approver_responses_id;
    }

    public void setPj_submittal_approver_responses_id(int pj_submittal_approver_responses_id) {
        this.pj_submittal_approver_responses_id = pj_submittal_approver_responses_id;
    }

    public List<AttachmentsSubmittals> getAttachmentsSubmittals() {
        return attachmentsSubmittals;
    }

    public void setAttachmentsSubmittals(List<AttachmentsSubmittals> attachmentsSubmittals) {
        this.attachmentsSubmittals = attachmentsSubmittals;
    }

    public int getPjSubmittalContactListId() {
        return pjSubmittalContactListId;
    }

    public void setPjSubmittalContactListId(int pjSubmittalContactListId) {
        this.pjSubmittalContactListId = pjSubmittalContactListId;
    }

    public int getPjSubmittalsId() {
        return pjSubmittalsId;
    }

    public void setPjSubmittalsId(int pjSubmittalsId) {
        this.pjSubmittalsId = pjSubmittalsId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}

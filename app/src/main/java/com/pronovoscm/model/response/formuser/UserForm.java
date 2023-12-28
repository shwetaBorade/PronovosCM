package com.pronovoscm.model.response.formuser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserForm {
    @SerializedName("publish")
    private int publish;
    @SerializedName("deleted_attachments")
    private List<DeletedAttachments> deletedAttachments;
    @SerializedName("updated_by_user")
    private String updatedByUser;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("user_form_mobile_id")
    private Long userFormMobileId;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("date_sent")
    private String dateSent;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("updated_by")
    private int updatedBy;
    @SerializedName("tenant_id")
    private int tenantId;
    @SerializedName("users_id")
    private int usersId;
    @SerializedName("submitted_data")
    private String submittedData;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("forms_id")
    private int formsId;

    @SerializedName("revision_number")
    private int revisionNumber;


    @SerializedName("scheduled_forms_id")
    private int scheduleFormId;
    @SerializedName("user_forms_id")
    private Long userFormsId;
    @SerializedName("pj_areas_id")
    private Integer pjAreasId;

    @SerializedName("email_status")
    private Integer emailStatus;

    public Integer getPjAreasId() {
        return pjAreasId;
    }

    public void setPjAreasId(Integer pjAreasId) {
        this.pjAreasId = pjAreasId;
    }

    public int getPublish() {
        return publish;
    }

    public void setPublish(int publish) {
        this.publish = publish;
    }

    public List<DeletedAttachments> getDeletedAttachments() {
        return deletedAttachments;
    }

    public void setDeletedAttachments(List<DeletedAttachments> deletedAttachments) {
        this.deletedAttachments = deletedAttachments;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getUserFormMobileId() {
        return userFormMobileId;
    }

    public void setUserFormMobileId(Long userFormMobileId) {
        this.userFormMobileId = userFormMobileId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public String getSubmittedData() {
        return submittedData;
    }

    public void setSubmittedData(String submittedData) {
        this.submittedData = submittedData;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getFormsId() {
        return formsId;
    }

    public void setFormsId(int formsId) {
        this.formsId = formsId;
    }

    public int getScheduleFormId() {
        return scheduleFormId;
    }

    public void setScheduleFormId(int scheduleFormId) {
        this.scheduleFormId = scheduleFormId;
    }

    public Long getUserFormsId() {
        return userFormsId;
    }

    public void setUserFormsId(Long userFormsId) {
        this.userFormsId = userFormsId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }
}

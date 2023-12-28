package com.pronovoscm.model.response.formscheduleresponse;

import com.google.gson.annotations.SerializedName;

public class ScheduledForms {
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("tenant_id")
    private int tenantId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("no_of_times")
    private int noOfTimes;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("recurrence")
    private String recurrence;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("form_id")
    private int formId;
    @SerializedName("scheduled_form_id")
    private int scheduledFormId;
    @SerializedName("deleted_at")
    private String deletedAt;

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

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNoOfTimes() {
        return noOfTimes;
    }

    public void setNoOfTimes(int noOfTimes) {
        this.noOfTimes = noOfTimes;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getScheduledFormId() {
        return scheduledFormId;
    }

    public void setScheduledFormId(int scheduledFormId) {
        this.scheduledFormId = scheduledFormId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

package com.pronovoscm.model.response.forms;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectForms {

    @SerializedName("original_forms_id")
    private int originalFormsId;
    @SerializedName("form_prefix")
    private String formPrefix;
    @SerializedName("form_name")
    private String formName;
    @SerializedName("form_id")
    private int formId;
    @SerializedName("active_revision")
    private int activeRevision;
    @SerializedName("revision_number")
    private int revisionNumber;
    @SerializedName("default_values")
    private String defaultValues;

    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("form_deleted_at")
    private String formDeletedAt;
    @SerializedName("form_categories_id")
    private int formCategoriesId;
    @SerializedName("users_id")
    private int usersId;
    @SerializedName("tenant_id")
    private int tenantId;
    @SerializedName("publish")
    private int publish;
    @SerializedName("form_sections")
    private String formSections;
    @SerializedName("revisions")
    private List<FormRevision> formRevisionList;

    public String getFormSections() {
        return formSections;
    }

    public void setFormSections(String formSections) {
        this.formSections = formSections;
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

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getFormCategoriesId() {
        return formCategoriesId;
    }

    public void setFormCategoriesId(int formCategoriesId) {
        this.formCategoriesId = formCategoriesId;
    }

    public int getPublish() {
        return publish;
    }

    public void setPublish(int publish) {
        this.publish = publish;
    }

    public String getFormPrefix() {
        return formPrefix;
    }

    public void setFormPrefix(String formPrefix) {
        this.formPrefix = formPrefix;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getFormDeletedAt() {
        return formDeletedAt;
    }

    public void setFormDeletedAt(String formDeletedAt) {
        this.formDeletedAt = formDeletedAt;
    }

    public int getOriginalFormsId() {
        return originalFormsId;
    }

    public void setOriginalFormsId(int originalFormsId) {
        this.originalFormsId = originalFormsId;
    }

    public int getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(int activeRevision) {
        this.activeRevision = activeRevision;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

    public List<FormRevision> getFormRevisionList() {
        return formRevisionList;
    }

    public void setFormRevisionList(List<FormRevision> formRevisionList) {
        this.formRevisionList = formRevisionList;
    }

    @Override
    public String toString() {
        return "ProjectForms{" +
                "originalFormsId=" + originalFormsId +
                ", formName='" + formName + '\'' +
                ", formId=" + formId +
                ", activeRevision=" + activeRevision +
                ", revisionNumber=" + revisionNumber +
                ", formDeletedAt='" + formDeletedAt + '\'' +
                ", publish=" + publish +
                ", formRevisionList=" + formRevisionList +
                "}\n";
    }
}

package com.pronovoscm.model.response.projectformcomponent;

import com.google.gson.annotations.SerializedName;

public class FormComponent {
    @SerializedName("component")
    private String component;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("form_categories_id")
    private int formCategoriesId;
    @SerializedName("form_id")
    private int formId;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
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

    public int getFormCategoriesId() {
        return formCategoriesId;
    }

    public void setFormCategoriesId(int formCategoriesId) {
        this.formCategoriesId = formCategoriesId;
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
}

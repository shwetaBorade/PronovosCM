package com.pronovoscm.model.response.formpermission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormPermissions {

    @SerializedName("form_permissions_id")
    @Expose
    private Long formPermissionsId;
    @SerializedName("forms_id")
    @Expose
    private Integer formsId;
    @SerializedName("pj_projects_id")
    @Expose
    private Long pjProjectsId;
    @SerializedName("users_id")
    @Expose
    private Integer usersId;
    @SerializedName("permissions_id")
    @Expose
    private int permissionsId;
    @SerializedName("is_active")
    @Expose
    private int isActive;
    @SerializedName("tenant_id")
    @Expose
    private Integer tenantId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;

    public Long getFormPermissionsId() {
        return formPermissionsId;
    }

    public void setFormPermissionsId(Long formPermissionsId) {
        this.formPermissionsId = formPermissionsId;
    }

    public Integer getFormsId() {
        return formsId;
    }

    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }

    public Long getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Long pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public int getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(int permissionsId) {
        this.permissionsId = permissionsId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
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

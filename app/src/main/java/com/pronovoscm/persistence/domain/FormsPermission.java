package com.pronovoscm.persistence.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "form_permissions")
public class FormsPermission {

    /* @Id(autoincrement = true)
     private Long id;*/
    @SerializedName("form_permissions_id")
    @Expose
    @Property(nameInDb = "form_permissions_id")
    @Id
    private Long formPermissionsId;
    @SerializedName("forms_id")
    @Expose
    private Integer formsId;
    @SerializedName("pj_projects_id")
    @Expose
    @Property(nameInDb = "pj_projects_id")
    private Long pjProjectsId;
    @SerializedName("users_id")
    @Expose
    @Property(nameInDb = "users_id")
    private Integer usersId;
    @SerializedName("permissions_id")
    @Expose
    @Property(nameInDb = "permissions_id")
    private Integer permissionsId;
    @SerializedName("is_active")
    @Expose
    @Property(nameInDb = "is_active")
    private Integer isActive;
    @SerializedName("tenant_id")
    @Expose
    @Property(nameInDb = "tenant_id")
    private Integer tenantId;
    @SerializedName("created_at")
    @Expose
    @Property(nameInDb = "created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    @Expose
    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @SerializedName("deleted_at")
    @Expose
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;


    @Generated(hash = 1840336234)
    public FormsPermission() {
    }

    @Generated(hash = 265458082)
    public FormsPermission(Long formPermissionsId, Integer formsId,
                           Long pjProjectsId, Integer usersId, Integer permissionsId,
                           Integer isActive, Integer tenantId, Date createdAt, Date updatedAt,
                           Date deletedAt) {
        this.formPermissionsId = formPermissionsId;
        this.formsId = formsId;
        this.pjProjectsId = pjProjectsId;
        this.usersId = usersId;
        this.permissionsId = permissionsId;
        this.isActive = isActive;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

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

    public Integer getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(Integer permissionsId) {
        this.permissionsId = permissionsId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}

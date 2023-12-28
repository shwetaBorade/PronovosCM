package com.pronovoscm.persistence.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

@Entity(nameInDb = "pj_documents_folders")
public class PjDocumentsFolders implements Serializable {

    public static final long serialVersionUID = 592870878;
    @SerializedName("pj_documents_folders_id")
    @Expose
    @Property(nameInDb = "pj_documents_folders_id")
    public Long pjDocumentsFoldersId;
    @SerializedName("parent_id")
    @Expose
    @Property(nameInDb = "parent_id")
    public Integer parentId;
    @SerializedName("pj_projects_id")
    @Expose
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @SerializedName("created_by")
    @Expose
    @Property(nameInDb = "created_by")
    public Integer createdBy;
    @SerializedName("updated_by")
    @Expose
    @Property(nameInDb = "updated_by")
    public Integer updatedBy;
    @SerializedName("tenant_id")
    @Expose
    @Property(nameInDb = "tenant_id")
    public Integer tenantId;
    @SerializedName("name")
    @Expose
    @Property(nameInDb = "name")
    public String name;
    @SerializedName("updated_at")
    @Expose
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @SerializedName("created_at")
    @Expose
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @SerializedName("deleted_at")
    @Expose
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @SerializedName("users_id")
    @Expose
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "lastupdatedate")
    Date lastupdatedate;
    @Id(autoincrement = true)
    private Long id;
    @SerializedName("is_default")
    @Expose
    @Property(nameInDb = "is_default")
    private Integer isDefault;

    @SerializedName("is_visible")
    @Expose
    @Property(nameInDb = "is_visible")
    private Integer isVisible;

    @SerializedName("is_private")
    @Expose
    @Property(nameInDb = "is_private")
    private Integer isPrivate;


    @Generated(hash = 1207021472)
    public PjDocumentsFolders(Long pjDocumentsFoldersId, Integer parentId,
                              Integer pjProjectsId, Integer createdBy, Integer updatedBy,
                              Integer tenantId, String name, Date updatedAt, Date createdAt,
                              Date deletedAt, Integer usersId, Date lastupdatedate, Long id,
                              Integer isDefault, Integer isVisible, Integer isPrivate) {
        this.pjDocumentsFoldersId = pjDocumentsFoldersId;
        this.parentId = parentId;
        this.pjProjectsId = pjProjectsId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.tenantId = tenantId;
        this.name = name;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.usersId = usersId;
        this.lastupdatedate = lastupdatedate;
        this.id = id;
        this.isDefault = isDefault;
        this.isVisible = isVisible;
        this.isPrivate = isPrivate;
    }

    @Generated(hash = 1952299770)
    public PjDocumentsFolders() {
    }



    public Long getPjDocumentsFoldersId() {
        return pjDocumentsFoldersId;
    }

    public void setPjDocumentsFoldersId(Long pjDocumentsFoldersId) {
        this.pjDocumentsFoldersId = pjDocumentsFoldersId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getLastupdatedate() {
        return this.lastupdatedate;
    }

    public void setLastupdatedate(Date lastupdatedate) {
        this.lastupdatedate = lastupdatedate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}

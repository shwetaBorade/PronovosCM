package com.pronovoscm.persistence.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_documents_files")
public class PjDocumentsFiles {
    @SerializedName("pj_documents_files_id")
    @Expose
    @Property(nameInDb = "pj_documents_files_id")
    public Long pjDocumentsFilesId;
    @SerializedName("original_pj_documents_files_id")
    @Expose
    @Property(nameInDb = "original_pj_documents_files_id")
    public Integer originalPjDocumentsFilesId;
    @SerializedName("pj_projects_id")
    @Expose
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @SerializedName("pj_documents_folders_id")
    @Expose
    @Property(nameInDb = "pj_documents_folders_id")
    public Integer pjDocumentsFoldersId;
    @SerializedName("name")
    @Expose
    @Property(nameInDb = "name")
    public String name;
    @SerializedName("original_name")
    @Expose
    @Property(nameInDb = "original_name")
    public String originalName;
    @SerializedName("type")
    @Expose
    @Property(nameInDb = "type")
    public String type;
    @SerializedName("revision_number")
    @Expose
    @Property(nameInDb = "revision_number")
    public Integer revisionNumber;
    @SerializedName("active_revision")
    @Expose
    @Property(nameInDb = "active_revision")
    public Integer activeRevision;
    @SerializedName("location")
    @Expose
    @Property(nameInDb = "location")
    public String location;
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
    @Property(nameInDb = "lastupdatedate")
    Date lastupdatedate;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "file_status")
    Integer fileStatus;
    @Id(autoincrement = true)
    private Long id;


    @SerializedName("is_visible")
    @Expose
    @Property(nameInDb = "is_visible")
    private Integer isVisible;

    @SerializedName("is_private")
    @Expose
    @Property(nameInDb = "is_private")
    private Integer isPrivate;


    @Generated(hash = 756223225)
    public PjDocumentsFiles(Long pjDocumentsFilesId,
                            Integer originalPjDocumentsFilesId, Integer pjProjectsId,
                            Integer pjDocumentsFoldersId, String name, String originalName,
                            String type, Integer revisionNumber, Integer activeRevision,
                            String location, Integer createdBy, Integer updatedBy, Integer tenantId,
                            Date updatedAt, Date createdAt, Date deletedAt, Date lastupdatedate,
                            Boolean isSync, Integer fileStatus, Long id, Integer isVisible,
                            Integer isPrivate) {
        this.pjDocumentsFilesId = pjDocumentsFilesId;
        this.originalPjDocumentsFilesId = originalPjDocumentsFilesId;
        this.pjProjectsId = pjProjectsId;
        this.pjDocumentsFoldersId = pjDocumentsFoldersId;
        this.name = name;
        this.originalName = originalName;
        this.type = type;
        this.revisionNumber = revisionNumber;
        this.activeRevision = activeRevision;
        this.location = location;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.tenantId = tenantId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.lastupdatedate = lastupdatedate;
        this.isSync = isSync;
        this.fileStatus = fileStatus;
        this.id = id;
        this.isVisible = isVisible;
        this.isPrivate = isPrivate;
    }

    @Generated(hash = 1864037097)
    public PjDocumentsFiles() {
    }


    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public Long getPjDocumentsFilesId() {
        return pjDocumentsFilesId;
    }

    public void setPjDocumentsFilesId(Long pjDocumentsFilesId) {
        this.pjDocumentsFilesId = pjDocumentsFilesId;
    }

    public Integer getOriginalPjDocumentsFilesId() {
        return originalPjDocumentsFilesId;
    }

    public void setOriginalPjDocumentsFilesId(Integer originalPjDocumentsFilesId) {
        this.originalPjDocumentsFilesId = originalPjDocumentsFilesId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getPjDocumentsFoldersId() {
        return pjDocumentsFoldersId;
    }

    public void setPjDocumentsFoldersId(Integer pjDocumentsFoldersId) {
        this.pjDocumentsFoldersId = pjDocumentsFoldersId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(Integer activeRevision) {
        this.activeRevision = activeRevision;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Override
    public String toString() {
        return "PjDocumentsFiles{" +
                "pjDocumentsFilesId=" + pjDocumentsFilesId +
                ", pjDocumentsFoldersId=" + pjDocumentsFoldersId +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }
}

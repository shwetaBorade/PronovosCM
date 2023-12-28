package com.pronovoscm.model.response.documents;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Documentsfile {
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("pj_documents_files_id")
    @Expose
    private Long pjDocumentsFilesId;
    @SerializedName("original_pj_documents_files_id")
    @Expose
    private Integer originalPjDocumentsFilesId;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;
    @SerializedName("pj_documents_folders_id")
    @Expose
    private Integer pjDocumentsFoldersId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("active_revision")
    @Expose
    private Integer activeRevision;
    @SerializedName("revision_number")
    @Expose
    private Integer revisionNumber;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("updated_by")
    @Expose
    private Integer updatedBy;
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

    @SerializedName("is_visible")
    @Expose
    private Integer isVisible;

    @SerializedName("is_private")
    @Expose
    private Integer isPrivate;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(Integer activeRevision) {
        this.activeRevision = activeRevision;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
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

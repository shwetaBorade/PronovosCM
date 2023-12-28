package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Drawing_folders")
public class DrawingFolders {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "drw_folders_id")
    Integer drwFoldersId;
    @Property(nameInDb = "folder_description")
    String folderDescription;
    @Property(nameInDb = "folder_name")
    String folderName;
    @Property(nameInDb = "lastupdatedate")
    Date lastupdatedate;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "sync_folder")
    Boolean syncFolder;
    @Property(nameInDb = "sync_drawing_folder")
    Boolean syncDrawingFolder;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "last_update_xml")
    Date lastUpdateXml;
    @Generated(hash = 1034660376)
    public DrawingFolders(Long id, Date createdAt, Date deletedAt,
            Integer drwFoldersId, String folderDescription, String folderName,
            Date lastupdatedate, Integer pjProjectsId, Boolean syncFolder,
            Boolean syncDrawingFolder, Date updatedAt, Integer usersId,
            Date lastUpdateXml) {
        this.id = id;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.drwFoldersId = drwFoldersId;
        this.folderDescription = folderDescription;
        this.folderName = folderName;
        this.lastupdatedate = lastupdatedate;
        this.pjProjectsId = pjProjectsId;
        this.syncFolder = syncFolder;
        this.syncDrawingFolder = syncDrawingFolder;
        this.updatedAt = updatedAt;
        this.usersId = usersId;
        this.lastUpdateXml = lastUpdateXml;
    }
    @Generated(hash = 633113300)
    public DrawingFolders() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Integer getDrwFoldersId() {
        return this.drwFoldersId;
    }
    public void setDrwFoldersId(Integer drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }
    public String getFolderDescription() {
        return this.folderDescription;
    }
    public void setFolderDescription(String folderDescription) {
        this.folderDescription = folderDescription;
    }
    public String getFolderName() {
        return this.folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public Date getLastupdatedate() {
        return this.lastupdatedate;
    }
    public void setLastupdatedate(Date lastupdatedate) {
        this.lastupdatedate = lastupdatedate;
    }
    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }
    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }
    public Boolean getSyncFolder() {
        return this.syncFolder;
    }
    public void setSyncFolder(Boolean syncFolder) {
        this.syncFolder = syncFolder;
    }
    public Boolean getSyncDrawingFolder() {
        return this.syncDrawingFolder;
    }
    public void setSyncDrawingFolder(Boolean syncDrawingFolder) {
        this.syncDrawingFolder = syncDrawingFolder;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Date getLastUpdateXml() {
        return this.lastUpdateXml;
    }
    public void setLastUpdateXml(Date lastUpdateXml) {
        this.lastUpdateXml = lastUpdateXml;
    }
}

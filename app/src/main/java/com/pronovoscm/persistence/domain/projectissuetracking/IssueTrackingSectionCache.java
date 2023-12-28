package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "issue_tracking_sections")
public class IssueTrackingSectionCache implements Serializable {
    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "section_id")
    private Long sectionId;
    @Property(nameInDb = "sections_id")
    private Integer issue_tracking_sections_id;
    @Property(nameInDb = "section_name")
    private String sectionName;
    @Property(nameInDb = "sort_order")
    private Integer sortOrder;
    @Property(nameInDb = "tenant_id")
    private Integer tenantId;
    @Property(nameInDb = "users_id")
    private Integer usersId;
    @Property(nameInDb = "created_at")
    private Date createdAt;
    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Generated(hash = 1554002791)
    public IssueTrackingSectionCache(Long sectionId,
            Integer issue_tracking_sections_id, String sectionName,
            Integer sortOrder, Integer tenantId, Integer usersId, Date createdAt,
            Date updatedAt, Date deletedAt) {
        this.sectionId = sectionId;
        this.issue_tracking_sections_id = issue_tracking_sections_id;
        this.sectionName = sectionName;
        this.sortOrder = sortOrder;
        this.tenantId = tenantId;
        this.usersId = usersId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 1783166136)
    public IssueTrackingSectionCache() {
    }
    public Long getSectionId() {
        return this.sectionId;
    }
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
    public Integer getIssue_tracking_sections_id() {
        return this.issue_tracking_sections_id;
    }
    public void setIssue_tracking_sections_id(Integer issue_tracking_sections_id) {
        this.issue_tracking_sections_id = issue_tracking_sections_id;
    }
    public String getSectionName() {
        return this.sectionName;
    }
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    public Integer getSortOrder() {
        return this.sortOrder;
    }
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    public Integer getTenantId() {
        return this.tenantId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}
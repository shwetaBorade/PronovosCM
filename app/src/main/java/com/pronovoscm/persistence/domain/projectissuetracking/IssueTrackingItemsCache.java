package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "issue_tracking_items")
public class IssueTrackingItemsCache implements Serializable {
    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "item_id")
    private Long itemId;
    @Property(nameInDb = "issue_tracking_items_id")
    private Integer issueTrackingItemsId;
    @Property(nameInDb = "issue_tracking_sections_id")
    private Integer issueTrackingSectionsId;
    @Property(nameInDb = "item_name")
    private String itemName;
    @Property(nameInDb = "tenant_id")
    private Integer tenantId;
    @Property(nameInDb = "users_id")
    private Integer usersId;
    @Property(nameInDb = "sort_order")
    private Integer sortOrder;
    @Property(nameInDb = "additional_data")
    private String additionalData;
    @Property(nameInDb = "tracking_item_types_id")
    private Integer trackingItemTypesId;
    @Property(nameInDb = "created_at")
    private Date created_at;
    @Property(nameInDb = "updated_at")
    private Date updated_at;
    @Property(nameInDb = "deleted_at")
    private Date deleted_at;
    @Generated(hash = 1077634767)
    public IssueTrackingItemsCache(Long itemId, Integer issueTrackingItemsId,
            Integer issueTrackingSectionsId, String itemName, Integer tenantId,
            Integer usersId, Integer sortOrder, String additionalData,
            Integer trackingItemTypesId, Date created_at, Date updated_at,
            Date deleted_at) {
        this.itemId = itemId;
        this.issueTrackingItemsId = issueTrackingItemsId;
        this.issueTrackingSectionsId = issueTrackingSectionsId;
        this.itemName = itemName;
        this.tenantId = tenantId;
        this.usersId = usersId;
        this.sortOrder = sortOrder;
        this.additionalData = additionalData;
        this.trackingItemTypesId = trackingItemTypesId;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }
    @Generated(hash = 45706252)
    public IssueTrackingItemsCache() {
    }
    public Long getItemId() {
        return this.itemId;
    }
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    public Integer getIssueTrackingItemsId() {
        return this.issueTrackingItemsId;
    }
    public void setIssueTrackingItemsId(Integer issueTrackingItemsId) {
        this.issueTrackingItemsId = issueTrackingItemsId;
    }
    public Integer getIssueTrackingSectionsId() {
        return this.issueTrackingSectionsId;
    }
    public void setIssueTrackingSectionsId(Integer issueTrackingSectionsId) {
        this.issueTrackingSectionsId = issueTrackingSectionsId;
    }
    public String getItemName() {
        return this.itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
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
    public Integer getSortOrder() {
        return this.sortOrder;
    }
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    public String getAdditionalData() {
        return this.additionalData;
    }
    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
    public Integer getTrackingItemTypesId() {
        return this.trackingItemTypesId;
    }
    public void setTrackingItemTypesId(Integer trackingItemTypesId) {
        this.trackingItemTypesId = trackingItemTypesId;
    }
    public Date getCreated_at() {
        return this.created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public Date getUpdated_at() {
        return this.updated_at;
    }
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
    public Date getDeleted_at() {
        return this.deleted_at;
    }
    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }
   
}
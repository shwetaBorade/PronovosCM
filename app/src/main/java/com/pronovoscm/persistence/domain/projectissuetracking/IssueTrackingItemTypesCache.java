package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "issue_tracking_item_types")
public class IssueTrackingItemTypesCache implements Serializable {
    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "item_id")
    private Long itemTypeId;
    @Property(nameInDb = "tracking_item_types_id")
    private Integer trackingItemTypesId;
    @Property(nameInDb = "label")
    private String label;
    @Property(nameInDb = "type")
    private String type;
    @Property(nameInDb = "options")
    private String options;
    @Property(nameInDb = "created_at")
    private Date createdAt;
    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Generated(hash = 1734218652)
    public IssueTrackingItemTypesCache(Long itemTypeId, Integer trackingItemTypesId,
            String label, String type, String options, Date createdAt,
            Date updatedAt, Date deletedAt) {
        this.itemTypeId = itemTypeId;
        this.trackingItemTypesId = trackingItemTypesId;
        this.label = label;
        this.type = type;
        this.options = options;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 1897069303)
    public IssueTrackingItemTypesCache() {
    }
    public Long getItemTypeId() {
        return this.itemTypeId;
    }
    public void setItemTypeId(Long itemTypeId) {
        this.itemTypeId = itemTypeId;
    }
    public Integer getTrackingItemTypesId() {
        return this.trackingItemTypesId;
    }
    public void setTrackingItemTypesId(Integer trackingItemTypesId) {
        this.trackingItemTypesId = trackingItemTypesId;
    }
    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getOptions() {
        return this.options;
    }
    public void setOptions(String options) {
        this.options = options;
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
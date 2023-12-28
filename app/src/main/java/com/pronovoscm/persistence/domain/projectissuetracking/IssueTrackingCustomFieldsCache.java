package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(nameInDb = "issue_tracking_custom_fields")
public class IssueTrackingCustomFieldsCache implements Serializable {
    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "custom_field_id")
    private Long customFieldId;
    @Property(nameInDb = "issue_tracking_custom_id")
    private Long issueTrackingCustomId;
    @Property(nameInDb = "issue_tracking_custom_mobile_id")
    private Long issueTrackingCustomMobileId;
    @Property(nameInDb = "issue_tracking_id")
    private Long issueTrackingId;
    @Property(nameInDb = "issue_tracking_mobile_id")
    private Long issueTrackingMobileId;
    @Property(nameInDb = "issue_section_id")
    private Integer issueSectionId;
    @Property(nameInDb = "issue_tracking_items_id")
    private Integer issueTrackingItemsId;
    @Property(nameInDb = "tracking_item_types_id")
    private Integer trackingItemTypesId;
    @Property(nameInDb = "value")
    private String value;
    @Property(nameInDb = "tenant_id")
    private Long tenantId;
    @Property(nameInDb = "users_id")
    private Long usersId;

    @Property(nameInDb = "is_sync")
    private Boolean isSync;

    @Generated(hash = 1411350000)
    public IssueTrackingCustomFieldsCache(Long customFieldId,
            Long issueTrackingCustomId, Long issueTrackingCustomMobileId,
            Long issueTrackingId, Long issueTrackingMobileId,
            Integer issueSectionId, Integer issueTrackingItemsId,
            Integer trackingItemTypesId, String value, Long tenantId, Long usersId,
            Boolean isSync) {
        this.customFieldId = customFieldId;
        this.issueTrackingCustomId = issueTrackingCustomId;
        this.issueTrackingCustomMobileId = issueTrackingCustomMobileId;
        this.issueTrackingId = issueTrackingId;
        this.issueTrackingMobileId = issueTrackingMobileId;
        this.issueSectionId = issueSectionId;
        this.issueTrackingItemsId = issueTrackingItemsId;
        this.trackingItemTypesId = trackingItemTypesId;
        this.value = value;
        this.tenantId = tenantId;
        this.usersId = usersId;
        this.isSync = isSync;
    }

    @Generated(hash = 1651366845)
    public IssueTrackingCustomFieldsCache() {
    }

    public Long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public Long getIssueTrackingCustomId() {
        return this.issueTrackingCustomId;
    }

    public void setIssueTrackingCustomId(Long issueTrackingCustomId) {
        this.issueTrackingCustomId = issueTrackingCustomId;
    }

    public Long getIssueTrackingCustomMobileId() {
        return this.issueTrackingCustomMobileId;
    }

    public void setIssueTrackingCustomMobileId(Long issueTrackingCustomMobileId) {
        this.issueTrackingCustomMobileId = issueTrackingCustomMobileId;
    }

    public Long getIssueTrackingId() {
        return this.issueTrackingId;
    }

    public void setIssueTrackingId(Long issueTrackingId) {
        this.issueTrackingId = issueTrackingId;
    }

    public Long getIssueTrackingMobileId() {
        return this.issueTrackingMobileId;
    }

    public void setIssueTrackingMobileId(Long issueTrackingMobileId) {
        this.issueTrackingMobileId = issueTrackingMobileId;
    }

    public Integer getIssueSectionId() {
        return this.issueSectionId;
    }

    public void setIssueSectionId(Integer issueSectionId) {
        this.issueSectionId = issueSectionId;
    }

    public Integer getIssueTrackingItemsId() {
        return this.issueTrackingItemsId;
    }

    public void setIssueTrackingItemsId(Integer issueTrackingItemsId) {
        this.issueTrackingItemsId = issueTrackingItemsId;
    }

    public Integer getTrackingItemTypesId() {
        return this.trackingItemTypesId;
    }

    public void setTrackingItemTypesId(Integer trackingItemTypesId) {
        this.trackingItemTypesId = trackingItemTypesId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUsersId() {
        return this.usersId;
    }

    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
}
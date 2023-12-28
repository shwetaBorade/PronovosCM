package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(nameInDb = "impacts_and_root_cause_master")
public class ImpactsAndRootCauseCache implements Serializable {

    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "cache_id")
    private Long cacheId = null;

    @Property(nameInDb = "pj_issue_cause_impact_id")
    private Long pjIssuesCauseImpactId = 0L;

    @Property(nameInDb = "name")
    private String name = "";

    @Property(nameInDb = "impact_status")
    private Integer impactStatus = 0;

    @Property(nameInDb = "created_at")
    private String createdAt = "";

    @Property(nameInDb = "updated_at")
    private String updatedAt = "";

    @Property(nameInDb = "icon_url")
    private String iconUrl = "";

    @Property(nameInDb = "mobile_icon")
    private String mobileIcon = "";

    @Property(nameInDb = "icon_storage_path")
    private String iconStoragePath = "";

    @Generated(hash = 1710533592)
    public ImpactsAndRootCauseCache(Long cacheId, Long pjIssuesCauseImpactId, String name, Integer impactStatus, String createdAt, String updatedAt, String iconUrl, String mobileIcon, String iconStoragePath) {
        this.cacheId = cacheId;
        this.pjIssuesCauseImpactId = pjIssuesCauseImpactId;
        this.name = name;
        this.impactStatus = impactStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.iconUrl = iconUrl;
        this.mobileIcon = mobileIcon;
        this.iconStoragePath = iconStoragePath;
    }

    @Generated(hash = 998433360)
    public ImpactsAndRootCauseCache() {
    }

    public Long getCacheId() {
        return cacheId;
    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public Long getPjIssuesCauseImpactId() {
        return pjIssuesCauseImpactId;
    }

    public void setPjIssuesCauseImpactId(Long pjIssuesCauseImpactId) {
        this.pjIssuesCauseImpactId = pjIssuesCauseImpactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImpactStatus() {
        return impactStatus;
    }

    public void setImpactStatus(Integer impactStatus) {
        this.impactStatus = impactStatus;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMobileIcon() {
        return mobileIcon;
    }

    public void setMobileIcon(String mobileIcon) {
        this.mobileIcon = mobileIcon;
    }

    public String getIconStoragePath() {
        return iconStoragePath;
    }

    public void setIconStoragePath(String iconStoragePath) {
        this.iconStoragePath = iconStoragePath;
    }
}
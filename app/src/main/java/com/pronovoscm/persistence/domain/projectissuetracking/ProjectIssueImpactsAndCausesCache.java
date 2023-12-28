package com.pronovoscm.persistence.domain.projectissuetracking;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "pj_issue_impacts_and_causes")
public class ProjectIssueImpactsAndCausesCache {

    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "cache_id")
    private Long cacheId;

    @Property(nameInDb = "pj_issues_trackings_id")
    private Long pjIssuesTrackingsId;

    @Property(nameInDb = "pj_issues_id")
    private Long pjIssuesId;

    @Property(nameInDb = "pj_issues_id_mobile")
    private Long pjIssuesIdMobile;

    @Property(nameInDb = "pj_issues_causeimpact_id")
    private Long pjIssuesCauseImpactId;

    @Property(nameInDb = "type")
    private Integer type;

    @Property(nameInDb = "pj_issues_trackings_id_mobile")
    private Long pjIssuesTrackingIdMobile;

    @Property(nameInDb = "user_id")
    private Long userId;

    @Property(nameInDb = "project_id")
    private Long projectId;

    @Property(nameInDb = "created_at")
    private String createdAt;

    @Property(nameInDb = "updated_at")
    private String updatedAt;

    @Property(nameInDb = "deleted_at")
    private String deletedAt;
    
    @Generated(hash = 2018524791)
    public ProjectIssueImpactsAndCausesCache(Long cacheId, Long pjIssuesTrackingsId, Long pjIssuesId, Long pjIssuesIdMobile, Long pjIssuesCauseImpactId, Integer type, Long pjIssuesTrackingIdMobile, Long userId, Long projectId, String createdAt, String updatedAt, String deletedAt) {
        this.cacheId = cacheId;
        this.pjIssuesTrackingsId = pjIssuesTrackingsId;
        this.pjIssuesId = pjIssuesId;
        this.pjIssuesIdMobile = pjIssuesIdMobile;
        this.pjIssuesCauseImpactId = pjIssuesCauseImpactId;
        this.type = type;
        this.pjIssuesTrackingIdMobile = pjIssuesTrackingIdMobile;
        this.userId = userId;
        this.projectId = projectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    @Generated(hash = 88276886)
    public ProjectIssueImpactsAndCausesCache() {
    }

    public Long getCacheId() {
        return cacheId;
    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public Long getPjIssuesTrackingsId() {
        return pjIssuesTrackingsId;
    }

    public void setPjIssuesTrackingsId(Long pjIssuesTrackingsId) {
        this.pjIssuesTrackingsId = pjIssuesTrackingsId;
    }

    public Long getPjIssuesId() {
        return pjIssuesId;
    }

    public void setPjIssuesId(Long pjIssuesId) {
        this.pjIssuesId = pjIssuesId;
    }

    public Long getPjIssuesIdMobile() {
        return pjIssuesIdMobile;
    }

    public void setPjIssuesIdMobile(Long pjIssuesIdMobile) {
        this.pjIssuesIdMobile = pjIssuesIdMobile;
    }

    public Long getPjIssuesCauseImpactId() {
        return pjIssuesCauseImpactId;
    }

    public void setPjIssuesCauseImpactId(Long pjIssuesCauseImpactId) {
        this.pjIssuesCauseImpactId = pjIssuesCauseImpactId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getPjIssuesTrackingIdMobile() {
        return pjIssuesTrackingIdMobile;
    }

    public void setPjIssuesTrackingIdMobile(Long pjIssuesTrackingIdMobile) {
        this.pjIssuesTrackingIdMobile = pjIssuesTrackingIdMobile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "project_issues_item_breakdown")
public class ProjectIssuesItemBreakdownCache {

    @Id(autoincrement = true)
    @Property(nameInDb = "cache_id")
    private Long cacheId;

    @Property(nameInDb = "pj_issues_item_breakdowns_id")
    private Long pjIssuesItemBreakdownId;

    @Property(nameInDb = "pj_issues_id")
    private Long pjIssuesId;

    @Property(nameInDb = "pj_issues_id_mobile")
    private Long pjIssuesIdMobile;

    @Property(nameInDb = "description")
    private String description;

    @Property(nameInDb = "days")
    private Integer days;

    @Property(nameInDb = "amount")
    private Long amount;

    @Property(nameInDb = "pj_issues_item_breakdowns_id_mobile")
    private Long pjIssuesItemBreakdownIdMobile;

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

    @Generated(hash = 981094971)
    public ProjectIssuesItemBreakdownCache(Long cacheId, Long pjIssuesItemBreakdownId, Long pjIssuesId, Long pjIssuesIdMobile, String description, Integer days, Long amount, Long pjIssuesItemBreakdownIdMobile, Long userId, Long projectId, String createdAt, String updatedAt, String deletedAt) {
        this.cacheId = cacheId;
        this.pjIssuesItemBreakdownId = pjIssuesItemBreakdownId;
        this.pjIssuesId = pjIssuesId;
        this.pjIssuesIdMobile = pjIssuesIdMobile;
        this.description = description;
        this.days = days;
        this.amount = amount;
        this.pjIssuesItemBreakdownIdMobile = pjIssuesItemBreakdownIdMobile;
        this.userId = userId;
        this.projectId = projectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    @Generated(hash = 394920257)
    public ProjectIssuesItemBreakdownCache() {
    }

    public Long getCacheId() {
        return cacheId;
    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public Long getPjIssuesItemBreakdownId() {
        return pjIssuesItemBreakdownId;
    }

    public void setPjIssuesItemBreakdownId(Long pjIssuesItemBreakdownId) {
        this.pjIssuesItemBreakdownId = pjIssuesItemBreakdownId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getPjIssuesItemBreakdownIdMobile() {
        return pjIssuesItemBreakdownIdMobile;
    }

    public void setPjIssuesItemBreakdownIdMobile(Long pjIssuesItemBreakdownIdMobile) {
        this.pjIssuesItemBreakdownIdMobile = pjIssuesItemBreakdownIdMobile;
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

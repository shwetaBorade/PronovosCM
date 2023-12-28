package com.pronovoscm.persistence.domain.projectissuetracking;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import com.pronovoscm.persistence.domain.DaoSession;

@Entity(nameInDb = "project_issues")
public class ProjectIssuesCache implements Serializable {

    public static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "cache_id")
    private Long cacheId;

    @Property(nameInDb = "pj_issues_id")
    private Long pjIssueId;

    @Property(nameInDb = "pj_projects_id")
    private Long pjProjectsId;

    @Property(nameInDb = "users_id")
    private Long usersId;

    @Property(nameInDb = "tenant_id")
    private Long tenantId;

    @Property(nameInDb = "issue_number")
    private String issueNumber;

    @Property(nameInDb = "title")
    private String title;

    @Property(nameInDb = "date_created")
    private String dateCreated;

    @Property(nameInDb = "date_resolved")
    private String dateResolved;

    @Property(nameInDb = "is_resolved")
    private Boolean isResolved;

    @Property(nameInDb = "description")
    private String description;

    @Property(nameInDb = "pj_issues_id_mobile")
    private Long pjIssueIdMobile;

    @Property(nameInDb = "created_at")
    private String createdAt;

    @Property(nameInDb = "updated_at")
    private String updatedAt;

    @Property(nameInDb = "deleted_at")
    private String deletedAt;

    @Property(nameInDb = "created_by")
    private Long createdBy;

    @Property(nameInDb = "is_in_process")
    private Boolean isInProcess;

    @Property(nameInDb = "is_sync")
    private Boolean isSync;

    @Property(nameInDb = "created_by_name")
    private String createdByName;
    @Property(nameInDb = "needed_by")
    private String needeBy;
    @Property(nameInDb = "assignee_id")
    private long assigneId;
    @Property(nameInDb = "assignee_name")
    private String assigneeName;
    @Property(nameInDb = "needed_by_timezone")
    private String neededByTimezone;

    @ToMany(joinProperties = {
            @JoinProperty(name = "pjIssueId", referencedName = "pjIssuesId"),
            @JoinProperty(name = "pjIssueIdMobile", referencedName = "pjIssuesIdMobile"),
    })
    private List<ProjectIssueImpactsAndCausesCache> impactsAndCausesCacheList;

    @ToMany(joinProperties = {
            @JoinProperty(name = "pjIssueId", referencedName = "pjIssuesId"),
            @JoinProperty(name = "pjIssueIdMobile", referencedName = "pjIssuesIdMobile"),
    })
    private List<ProjectIssuesItemBreakdownCache> breakdownCacheList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 143158507)
    private transient ProjectIssuesCacheDao myDao;

    @Generated(hash = 84345604)
    public ProjectIssuesCache(Long cacheId, Long pjIssueId, Long pjProjectsId, Long usersId,
                              Long tenantId, String issueNumber, String title, String dateCreated,
                              String dateResolved, Boolean isResolved, String description, Long pjIssueIdMobile,
                              String createdAt, String updatedAt, String deletedAt, Long createdBy,
                              Boolean isInProcess, Boolean isSync, String createdByName, String needeBy,
                              long assigneId, String assigneeName, String neededByTimezone) {
        this.cacheId = cacheId;
        this.pjIssueId = pjIssueId;
        this.pjProjectsId = pjProjectsId;
        this.usersId = usersId;
        this.tenantId = tenantId;
        this.issueNumber = issueNumber;
        this.title = title;
        this.dateCreated = dateCreated;
        this.dateResolved = dateResolved;
        this.isResolved = isResolved;
        this.description = description;
        this.pjIssueIdMobile = pjIssueIdMobile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.isInProcess = isInProcess;
        this.isSync = isSync;
        this.createdByName = createdByName;
        this.needeBy = needeBy;
        this.assigneId = assigneId;
        this.assigneeName = assigneeName;
        this.neededByTimezone = neededByTimezone;
    }

    @Generated(hash = 732833034)
    public ProjectIssuesCache() {
    }

    public Long getCacheId() {
        return this.cacheId;
    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public Long getPjIssueId() {
        return this.pjIssueId;
    }

    public void setPjIssueId(Long pjIssueId) {
        this.pjIssueId = pjIssueId;
    }

    public Long getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Long pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Long getUsersId() {
        return this.usersId;
    }

    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getIssueNumber() {
        return this.issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateResolved() {
        return this.dateResolved;
    }

    public void setDateResolved(String dateResolved) {
        this.dateResolved = dateResolved;
    }

//    public Boolean getIsResolved() {
//        return this.isResolved;
//    }
//
//    public void setIsResolved(Boolean isResolved) {
//        this.isResolved = isResolved;
//    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPjIssueIdMobile() {
        return this.pjIssueIdMobile;
    }

    public void setPjIssueIdMobile(Long pjIssueIdMobile) {
        this.pjIssueIdMobile = pjIssueIdMobile;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getInProcess() {
        return isInProcess;
    }

    public void setInProcess(Boolean inProcess) {
        isInProcess = inProcess;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

//    public String getCreatedByName() {
//        return createdByName;
//    }
//
//    public void setCreatedByName(String createdByName) {
//        this.createdByName = createdByName;
//    }

    public Boolean getIsResolved() {
        return this.isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public Boolean getIsInProcess() {
        return this.isInProcess;
    }

    public void setIsInProcess(Boolean isInProcess) {
        this.isInProcess = isInProcess;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getNeedeBy() {
        return this.needeBy;
    }

    public void setNeedeBy(String needeBy) {
        this.needeBy = needeBy;
    }

    public long getAssigneId() {
        return this.assigneId;
    }

    public void setAssigneId(long assigneId) {
        this.assigneId = assigneId;
    }

    public String getAssigneeName() {
        return this.assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getNeededByTimezone() {
        return this.neededByTimezone;
    }

    public void setNeededByTimezone(String neededByTimezone) {
        this.neededByTimezone = neededByTimezone;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 949781416)
    public List<ProjectIssueImpactsAndCausesCache> getImpactsAndCausesCacheList() {
        if (impactsAndCausesCacheList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProjectIssueImpactsAndCausesCacheDao targetDao = daoSession
                    .getProjectIssueImpactsAndCausesCacheDao();
            List<ProjectIssueImpactsAndCausesCache> impactsAndCausesCacheListNew = targetDao
                    ._queryProjectIssuesCache_ImpactsAndCausesCacheList(pjIssueId,
                            pjIssueIdMobile);
            synchronized (this) {
                if (impactsAndCausesCacheList == null) {
                    impactsAndCausesCacheList = impactsAndCausesCacheListNew;
                }
            }
        }
        return impactsAndCausesCacheList;
    }
    public void setImpactsAndCausesCacheList(List<ProjectIssueImpactsAndCausesCache> impactsAndCausesCacheList) {
        this.impactsAndCausesCacheList = impactsAndCausesCacheList;
    }
    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 829636483)
    public synchronized void resetImpactsAndCausesCacheList() {
        impactsAndCausesCacheList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 774914149)
    public List<ProjectIssuesItemBreakdownCache> getBreakdownCacheList() {
        if (breakdownCacheList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProjectIssuesItemBreakdownCacheDao targetDao = daoSession
                    .getProjectIssuesItemBreakdownCacheDao();
            List<ProjectIssuesItemBreakdownCache> breakdownCacheListNew = targetDao
                    ._queryProjectIssuesCache_BreakdownCacheList(pjIssueId, pjIssueIdMobile);
            synchronized (this) {
                if (breakdownCacheList == null) {
                    breakdownCacheList = breakdownCacheListNew;
                }
            }
        }
        return breakdownCacheList;
    }
    public void setBreakdownCacheList(List<ProjectIssuesItemBreakdownCache> breakdownCacheList) {
        this.breakdownCacheList = breakdownCacheList;
    }
    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 444289946)
    public synchronized void resetBreakdownCacheList() {
        breakdownCacheList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 260876656)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProjectIssuesCacheDao() : null;
    }


}
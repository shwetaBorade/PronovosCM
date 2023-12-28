package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "CrewList")
public class CrewList {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "type")
    String type;
    @Property(nameInDb = "trades_id")
    Integer tradesId;
    @Property(nameInDb = "trade")
    String trade;
    @Property(nameInDb = "supt")
    Integer supt;
    @Property(nameInDb = "project_id")
    Integer projectId;
    @Property(nameInDb = "journeyman")
    Integer journeyman;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "foreman")
    Integer foreman;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "crew_report_id_mobile")
    Long crewReportIdMobile;
    @Property(nameInDb = "crew_report_id")
    Integer crewReportId;
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "companyname")
    String companyName;
    @Property(nameInDb = "company_id")
    Integer companyId;
    @Property(nameInDb = "apprentice")
    Integer apprentice;
    @Generated(hash = 1757524713)
    public CrewList(Integer usersId, String type, Integer tradesId, String trade,
            Integer supt, Integer projectId, Integer journeyman, Boolean isSync,
            Integer foreman, Date deletedAt, Long crewReportIdMobile,
            Integer crewReportId, Date createdAt, String companyName,
            Integer companyId, Integer apprentice) {
        this.usersId = usersId;
        this.type = type;
        this.tradesId = tradesId;
        this.trade = trade;
        this.supt = supt;
        this.projectId = projectId;
        this.journeyman = journeyman;
        this.isSync = isSync;
        this.foreman = foreman;
        this.deletedAt = deletedAt;
        this.crewReportIdMobile = crewReportIdMobile;
        this.crewReportId = crewReportId;
        this.createdAt = createdAt;
        this.companyName = companyName;
        this.companyId = companyId;
        this.apprentice = apprentice;
    }
    @Generated(hash = 822273513)
    public CrewList() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getTradesId() {
        return this.tradesId;
    }
    public void setTradesId(Integer tradesId) {
        this.tradesId = tradesId;
    }
    public String getTrade() {
        return this.trade;
    }
    public void setTrade(String trade) {
        this.trade = trade;
    }
    public Integer getSupt() {
        return this.supt;
    }
    public void setSupt(Integer supt) {
        this.supt = supt;
    }
    public Integer getProjectId() {
        return this.projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Integer getJourneyman() {
        return this.journeyman;
    }
    public void setJourneyman(Integer journeyman) {
        this.journeyman = journeyman;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public Integer getForeman() {
        return this.foreman;
    }
    public void setForeman(Integer foreman) {
        this.foreman = foreman;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Long getCrewReportIdMobile() {
        return this.crewReportIdMobile;
    }
    public void setCrewReportIdMobile(Long crewReportIdMobile) {
        this.crewReportIdMobile = crewReportIdMobile;
    }
    public Integer getCrewReportId() {
        return this.crewReportId;
    }
    public void setCrewReportId(Integer crewReportId) {
        this.crewReportId = crewReportId;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getCompanyName() {
        return this.companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public Integer getCompanyId() {
        return this.companyId;
    }
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public Integer getApprentice() {
        return this.apprentice;
    }
    public void setApprentice(Integer apprentice) {
        this.apprentice = apprentice;
    }
   
}

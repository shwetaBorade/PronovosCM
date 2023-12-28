package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WeatherReport")
public class WeatherReport {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "notes")
    String notes;
    @Property(nameInDb = "impact")
    String impact;
    @Property(nameInDb = "conditions")
    String conditions;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "project_id")
    Integer projectId;
    @Property(nameInDb = "report_date")
    Date reportDate;
    @Generated(hash = 1338668173)
    public WeatherReport(Long id, Integer usersId, String notes, String impact,
            String conditions, Boolean isSync, Integer projectId, Date reportDate) {
        this.id = id;
        this.usersId = usersId;
        this.notes = notes;
        this.impact = impact;
        this.conditions = conditions;
        this.isSync = isSync;
        this.projectId = projectId;
        this.reportDate = reportDate;
    }
    @Generated(hash = 1339715018)
    public WeatherReport() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getImpact() {
        return this.impact;
    }
    public void setImpact(String impact) {
        this.impact = impact;
    }
    public String getConditions() {
        return this.conditions;
    }
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public Integer getProjectId() {
        return this.projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Date getReportDate() {
        return this.reportDate;
    }
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}

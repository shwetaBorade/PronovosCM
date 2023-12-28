package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WorkImpact")
public class WorkImpact {


    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "is_attachment_sync")
    Boolean isAttachmentSync;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "companyname")
    String companyName;
    @Property(nameInDb = "company_id")
    Integer companyId;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "work_impact_report_id_mobile")
    Long workImpactReportIdMobile;
    @Property(nameInDb = "work_impact_report_id")
    Integer workImpactReportId;
    @Property(nameInDb = "work_imp_location")
    String workImpLocation;
    @Property(nameInDb = "work_summary")
    String workSummary;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "type")
    String type;
    @Property(nameInDb = "project_id")
    Integer projectId;
    @Generated(hash = 455263881)
    public WorkImpact(Boolean isSync, Boolean isAttachmentSync, Date deletedAt,
            Date createdAt, String companyName, Integer companyId,
            Long workImpactReportIdMobile, Integer workImpactReportId,
            String workImpLocation, String workSummary, Integer usersId,
            String type, Integer projectId) {
        this.isSync = isSync;
        this.isAttachmentSync = isAttachmentSync;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.companyName = companyName;
        this.companyId = companyId;
        this.workImpactReportIdMobile = workImpactReportIdMobile;
        this.workImpactReportId = workImpactReportId;
        this.workImpLocation = workImpLocation;
        this.workSummary = workSummary;
        this.usersId = usersId;
        this.type = type;
        this.projectId = projectId;
    }
    @Generated(hash = 623057347)
    public WorkImpact() {
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public Boolean getIsAttachmentSync() {
        return this.isAttachmentSync;
    }
    public void setIsAttachmentSync(Boolean isAttachmentSync) {
        this.isAttachmentSync = isAttachmentSync;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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
    public Long getWorkImpactReportIdMobile() {
        return this.workImpactReportIdMobile;
    }
    public void setWorkImpactReportIdMobile(Long workImpactReportIdMobile) {
        this.workImpactReportIdMobile = workImpactReportIdMobile;
    }
    public Integer getWorkImpactReportId() {
        return this.workImpactReportId;
    }
    public void setWorkImpactReportId(Integer workImpactReportId) {
        this.workImpactReportId = workImpactReportId;
    }
    public String getWorkImpLocation() {
        return this.workImpLocation;
    }
    public void setWorkImpLocation(String workImpLocation) {
        this.workImpLocation = workImpLocation;
    }
    public String getWorkSummary() {
        return this.workSummary;
    }
    public void setWorkSummary(String workSummary) {
        this.workSummary = workSummary;
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
    public Integer getProjectId() {
        return this.projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
   
}

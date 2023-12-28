package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WorkDetails")
public class WorkDetails {


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
    @Property(nameInDb = "work_details_report_id_mobile")
    Long workDetailsReportIdMobile;
    @Property(nameInDb = "work_details_report_id")
    Integer workDetailsReportId;
    @Property(nameInDb = "work_det_location")
    String workDetLocation;
    @Property(nameInDb = "work_summary")
    String workSummary;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "type")
    String type;
    @Property(nameInDb = "project_id")
    Integer projectId;
    @Generated(hash = 396926104)
    public WorkDetails(Boolean isSync, Boolean isAttachmentSync, Date deletedAt,
            Date createdAt, String companyName, Integer companyId,
            Long workDetailsReportIdMobile, Integer workDetailsReportId,
            String workDetLocation, String workSummary, Integer usersId,
            String type, Integer projectId) {
        this.isSync = isSync;
        this.isAttachmentSync = isAttachmentSync;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.companyName = companyName;
        this.companyId = companyId;
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
        this.workDetailsReportId = workDetailsReportId;
        this.workDetLocation = workDetLocation;
        this.workSummary = workSummary;
        this.usersId = usersId;
        this.type = type;
        this.projectId = projectId;
    }
    @Generated(hash = 1409053443)
    public WorkDetails() {
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
    public Long getWorkDetailsReportIdMobile() {
        return this.workDetailsReportIdMobile;
    }
    public void setWorkDetailsReportIdMobile(Long workDetailsReportIdMobile) {
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
    }
    public Integer getWorkDetailsReportId() {
        return this.workDetailsReportId;
    }
    public void setWorkDetailsReportId(Integer workDetailsReportId) {
        this.workDetailsReportId = workDetailsReportId;
    }
    public String getWorkDetLocation() {
        return this.workDetLocation;
    }
    public void setWorkDetLocation(String workDetLocation) {
        this.workDetLocation = workDetLocation;
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

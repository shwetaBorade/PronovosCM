package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "forms_schedule")
public class FormsSchedule {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Property(nameInDb = "created_at")
    private Date CreatedAt;
    @Property(nameInDb = "end_date")
    private Date endDate;
    @Property(nameInDb = "start_date")
    private Date startDate;
    @Property(nameInDb = "forms_id")
    private Integer formsId;
    @Property(nameInDb = "no_of_times")
    private Integer noOfTimes;
    @Property(nameInDb = "pj_project_id")
    private Integer pjProjectId;
    @Property(nameInDb = "recurrence")
    private String recurrence;
    @Property(nameInDb = "tenant_id")
    private Integer tenantId;
    @Property(nameInDb = "scheduled_form_id")
    private Integer scheduledFormId;

    @Generated(hash = 2040061484)
    public FormsSchedule(Long id, Date UpdatedAt, Date CreatedAt, Date endDate,
            Date startDate, Integer formsId, Integer noOfTimes, Integer pjProjectId,
            String recurrence, Integer tenantId, Integer scheduledFormId) {
        this.id = id;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.endDate = endDate;
        this.startDate = startDate;
        this.formsId = formsId;
        this.noOfTimes = noOfTimes;
        this.pjProjectId = pjProjectId;
        this.recurrence = recurrence;
        this.tenantId = tenantId;
        this.scheduledFormId = scheduledFormId;
    }
    @Generated(hash = 1111568719)
    public FormsSchedule() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getUpdatedAt() {
        return this.UpdatedAt;
    }
    public void setUpdatedAt(Date UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }
    public Date getCreatedAt() {
        return this.CreatedAt;
    }
    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }
    public Date getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Integer getFormsId() {
        return this.formsId;
    }
    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }
    public Integer getNoOfTimes() {
        return this.noOfTimes;
    }
    public void setNoOfTimes(Integer noOfTimes) {
        this.noOfTimes = noOfTimes;
    }
    public Integer getPjProjectId() {
        return this.pjProjectId;
    }
    public void setPjProjectId(Integer pjProjectId) {
        this.pjProjectId = pjProjectId;
    }
    public String getRecurrence() {
        return this.recurrence;
    }
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
    public Integer getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getScheduledFormId() {
        return this.scheduledFormId;
    }

    public void setScheduledFormId(Integer scheduledFormId) {
        this.scheduledFormId = scheduledFormId;
    }

    @Override
    public String toString() {
        return "FormsSchedule{" +
                "id=" + id +
                ", startDate=" + startDate +
                " \n CreatedAt  " + CreatedAt +
                ", formsId=" + formsId +
                ", pjProjectId=" + pjProjectId +
                '}';
    }
}

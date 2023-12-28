package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "project_form")
public class ProjectForm {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "form_last_updated_date")
    private Date formLastUpdatedDate;
    @Property(nameInDb = "form_component_last_updated_date")
    private Date formComponentLastUpdatedDate;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "forms_id")
    private Integer formsId;
    @Generated(hash = 1697122810)
    public ProjectForm(Long id, Date deletedAt, Date formLastUpdatedDate,
            Date formComponentLastUpdatedDate, Integer PjProjectsId,
            Integer formsId) {
        this.id = id;
        this.deletedAt = deletedAt;
        this.formLastUpdatedDate = formLastUpdatedDate;
        this.formComponentLastUpdatedDate = formComponentLastUpdatedDate;
        this.PjProjectsId = PjProjectsId;
        this.formsId = formsId;
    }
    @Generated(hash = 1773675287)
    public ProjectForm() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Date getFormLastUpdatedDate() {
        return this.formLastUpdatedDate;
    }
    public void setFormLastUpdatedDate(Date formLastUpdatedDate) {
        this.formLastUpdatedDate = formLastUpdatedDate;
    }
    public Date getFormComponentLastUpdatedDate() {
        return this.formComponentLastUpdatedDate;
    }
    public void setFormComponentLastUpdatedDate(Date formComponentLastUpdatedDate) {
        this.formComponentLastUpdatedDate = formComponentLastUpdatedDate;
    }
    public Integer getPjProjectsId() {
        return this.PjProjectsId;
    }
    public void setPjProjectsId(Integer PjProjectsId) {
        this.PjProjectsId = PjProjectsId;
    }
    public Integer getFormsId() {
        return this.formsId;
    }
    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }
  }

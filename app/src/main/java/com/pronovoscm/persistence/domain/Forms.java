package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "forms")
public class Forms {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "updated_at")
    public Date UpdatedAt;
    @Property(nameInDb = "created_at")
    public Date CreatedAt;
    @Property(nameInDb = "tenant_id")
    public Integer tenantId;
    @Property(nameInDb = "publish")
    public Integer publish;
    @Property(nameInDb = "form_prefix")
    public String formPrefix;
    @Property(nameInDb = "form_name")
    public String formName;
    @Property(nameInDb = "forms_id")
    public Integer formsId;
    @Property(nameInDb = "form_categories_id")
    public Integer formCategoriesId;
    @Property(nameInDb = "form_deleted_at")
    public Date formDeletedAt;

    @Property(nameInDb = "form_sections")
    public String formSections;


    @Property(nameInDb = "default_values")
    public String defaultValues;
    @Property(nameInDb = "original_forms_id")
    public Integer originalFormsId;
    @Property(nameInDb = "revision_number")
    public Integer revisionNumber;
    @Property(nameInDb = "active_revision")
    public Integer activeRevision;


    @Generated(hash = 613568735)
    public Forms() {
    }


    @Generated(hash = 128628243)
    public Forms(Long id, Date UpdatedAt, Date CreatedAt, Integer tenantId,
                 Integer publish, String formPrefix, String formName, Integer formsId,
                 Integer formCategoriesId, Date formDeletedAt, String formSections,
                 String defaultValues, Integer originalFormsId, Integer revisionNumber,
                 Integer activeRevision) {
        this.id = id;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.tenantId = tenantId;
        this.publish = publish;
        this.formPrefix = formPrefix;
        this.formName = formName;
        this.formsId = formsId;
        this.formCategoriesId = formCategoriesId;
        this.formDeletedAt = formDeletedAt;
        this.formSections = formSections;
        this.defaultValues = defaultValues;
        this.originalFormsId = originalFormsId;
        this.revisionNumber = revisionNumber;
        this.activeRevision = activeRevision;
    }


    public String getFormSections() {
        return formSections;
    }

    public void setFormSections(String formSections) {
        this.formSections = formSections;
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

    public Integer getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getPublish() {
        return this.publish;
    }

    public void setPublish(Integer publish) {
        this.publish = publish;
    }

    public String getFormPrefix() {
        return this.formPrefix;
    }

    public void setFormPrefix(String formPrefix) {
        this.formPrefix = formPrefix;
    }

    public String getFormName() {
        return this.formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Integer getFormsId() {
        return this.formsId;
    }

    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }

    public Integer getFormCategoriesId() {
        return this.formCategoriesId;
    }

    public void setFormCategoriesId(Integer formCategoriesId) {
        this.formCategoriesId = formCategoriesId;
    }

    public Date getFormDeletedAt() {
        return this.formDeletedAt;
    }

    public void setFormDeletedAt(Date formDeletedAt) {
        this.formDeletedAt = formDeletedAt;
    }


    public String getDefaultValues() {
        return this.defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Integer getOriginalFormsId() {
        return this.originalFormsId;
    }

    public void setOriginalFormsId(Integer originalFormsId) {
        this.originalFormsId = originalFormsId;
    }

    public Integer getRevisionNumber() {
        return this.revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getActiveRevision() {
        return this.activeRevision;
    }

    public void setActiveRevision(Integer activeRevision) {
        this.activeRevision = activeRevision;
    }

    @Override
    public String toString() {
        return "Forms{" +
                "id=" + id +
                ", formName='" + formName + '\'' +
                ", formsId=" + formsId +
              //  ", defaultValues='" + defaultValues + '\'' +
                ", originalFormsId=" + originalFormsId +
                ", revisionNumber=" + revisionNumber +
                ", activeRevision=" + activeRevision +
                '}';
    }
}

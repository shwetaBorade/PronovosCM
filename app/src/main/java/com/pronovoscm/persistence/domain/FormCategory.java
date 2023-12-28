package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "form_category")
public class FormCategory {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "category_name")
    private String categoryName;
    @Property(nameInDb = "form_categories_id")
    private Integer formCategoriesId;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Property(nameInDb = "created_at")
    private Date CreatedAt;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "category_tenant_id")
    public Integer tenantId;
    @Property(nameInDb = "is_default")
    private Integer isDefault;


    @Generated(hash = 1549930198)
    public FormCategory() {
    }

    @Generated(hash = 1205338746)
    public FormCategory(Long id, String categoryName, Integer formCategoriesId,
                        Date UpdatedAt, Date CreatedAt, Date deletedAt, Integer tenantId,
                        Integer isDefault) {
        this.id = id;
        this.categoryName = categoryName;
        this.formCategoriesId = formCategoriesId;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.deletedAt = deletedAt;
        this.tenantId = tenantId;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public Integer getFormCategoriesId() {
        return this.formCategoriesId;
    }
    public void setFormCategoriesId(Integer formCategoriesId) {
        this.formCategoriesId = formCategoriesId;
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

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

}

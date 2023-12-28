package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

@Entity(nameInDb = "eq_sub_categories")
public class EquipmentSubCategoriesMaster implements Serializable {
    public static final long serialVersionUID = 536871008;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "eq_categories_id")
    Integer eqCategoryId;
    @Property(nameInDb = "eq_sub_categories_id")
    Integer eqSubCategoryId;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "tenantId")
    Integer tenant_id;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Generated(hash = 1612796250)
    public EquipmentSubCategoriesMaster(Long id, String name, Date updatedAt,
            Integer eqCategoryId, Integer eqSubCategoryId, Integer usersId,
            Integer tenant_id, Date deletedAt) {
        this.id = id;
        this.name = name;
        this.updatedAt = updatedAt;
        this.eqCategoryId = eqCategoryId;
        this.eqSubCategoryId = eqSubCategoryId;
        this.usersId = usersId;
        this.tenant_id = tenant_id;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 1492701059)
    public EquipmentSubCategoriesMaster() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Integer getEqCategoryId() {
        return this.eqCategoryId;
    }
    public void setEqCategoryId(Integer eqCategoryId) {
        this.eqCategoryId = eqCategoryId;
    }
    public Integer getEqSubCategoryId() {
        return this.eqSubCategoryId;
    }
    public void setEqSubCategoryId(Integer eqSubCategoryId) {
        this.eqSubCategoryId = eqSubCategoryId;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getTenant_id() {
        return this.tenant_id;
    }
    public void setTenant_id(Integer tenant_id) {
        this.tenant_id = tenant_id;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}

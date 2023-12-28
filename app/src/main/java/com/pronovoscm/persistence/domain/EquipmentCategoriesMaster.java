package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

@Entity(nameInDb = "eq_categories")
public class EquipmentCategoriesMaster implements Serializable {

    public static final long serialVersionUID = 536871008;

    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "eq_categories_id")
    Integer eq_categories_id;
    @Property(nameInDb = "tenantId")
    Integer tenantId;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "allocation_uom")
    String allocation_uom;
    @Generated(hash = 1569184039)
    public EquipmentCategoriesMaster(Integer usersId, Long id, String name,
            Integer eq_categories_id, Integer tenantId, Date updatedAt,
            Date deletedAt, String allocation_uom) {
        this.usersId = usersId;
        this.id = id;
        this.name = name;
        this.eq_categories_id = eq_categories_id;
        this.tenantId = tenantId;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.allocation_uom = allocation_uom;
    }
    @Generated(hash = 1575779371)
    public EquipmentCategoriesMaster() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
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
    public Integer getEq_categories_id() {
        return this.eq_categories_id;
    }
    public void setEq_categories_id(Integer eq_categories_id) {
        this.eq_categories_id = eq_categories_id;
    }
    public Integer getTenantId() {
        return this.tenantId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public String getAllocation_uom() {
        return this.allocation_uom;
    }
    public void setAllocation_uom(String allocation_uom) {
        this.allocation_uom = allocation_uom;
    }

}

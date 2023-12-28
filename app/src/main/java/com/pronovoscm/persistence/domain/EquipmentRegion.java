package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "eq_region_equipment")
public class EquipmentRegion implements Serializable {
    public static final long serialVersionUID = 536871008;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "eq_region_equipment_id")
    Integer eqRegionEquipentId;
    @Property(nameInDb = "eq_sub_categories_id")
    Integer eqSubCategoryId;
    @Property(nameInDb = "regions_id")
    Integer regionsId;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "manufacturer")
    String manufacturer;
    @Property(nameInDb = "model")
    String model;
    @Property(nameInDb = "upc")
    String upc;
    @Property(nameInDb = "weight")
    String weight;
    @Property(nameInDb = "items_per_unit")
    String itemsPerUnit;
    @Property(nameInDb = "notes")
    String notes;
    @Property(nameInDb = "type")
    String type;
    @Property(nameInDb = "forecasted")
    Integer forecasted;
    @Property(nameInDb = "allocated")
    Integer allocated;
    @Property(nameInDb = "allocated_qty")
    String allocated_qty;
    @Property(nameInDb = "picture")
    String picture;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "tenantId")
    Integer tenant_id;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Generated(hash = 116292041)
    public EquipmentRegion(Long id, Integer eqRegionEquipentId,
            Integer eqSubCategoryId, Integer regionsId, String name,
            String manufacturer, String model, String upc, String weight,
            String itemsPerUnit, String notes, String type, Integer forecasted,
            Integer allocated, String allocated_qty, String picture, Date updatedAt,
            Integer usersId, Integer tenant_id, Date deletedAt) {
        this.id = id;
        this.eqRegionEquipentId = eqRegionEquipentId;
        this.eqSubCategoryId = eqSubCategoryId;
        this.regionsId = regionsId;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.upc = upc;
        this.weight = weight;
        this.itemsPerUnit = itemsPerUnit;
        this.notes = notes;
        this.type = type;
        this.forecasted = forecasted;
        this.allocated = allocated;
        this.allocated_qty = allocated_qty;
        this.picture = picture;
        this.updatedAt = updatedAt;
        this.usersId = usersId;
        this.tenant_id = tenant_id;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 1332774835)
    public EquipmentRegion() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getEqRegionEquipentId() {
        return this.eqRegionEquipentId;
    }
    public void setEqRegionEquipentId(Integer eqRegionEquipentId) {
        this.eqRegionEquipentId = eqRegionEquipentId;
    }
    public Integer getEqSubCategoryId() {
        return this.eqSubCategoryId;
    }
    public void setEqSubCategoryId(Integer eqSubCategoryId) {
        this.eqSubCategoryId = eqSubCategoryId;
    }
    public Integer getRegionsId() {
        return this.regionsId;
    }
    public void setRegionsId(Integer regionsId) {
        this.regionsId = regionsId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getManufacturer() {
        return this.manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return this.model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getUpc() {
        return this.upc;
    }
    public void setUpc(String upc) {
        this.upc = upc;
    }
    public String getWeight() {
        return this.weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getItemsPerUnit() {
        return this.itemsPerUnit;
    }
    public void setItemsPerUnit(String itemsPerUnit) {
        this.itemsPerUnit = itemsPerUnit;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getForecasted() {
        return this.forecasted;
    }
    public void setForecasted(Integer forecasted) {
        this.forecasted = forecasted;
    }
    public Integer getAllocated() {
        return this.allocated;
    }
    public void setAllocated(Integer allocated) {
        this.allocated = allocated;
    }
    public String getAllocated_qty() {
        return this.allocated_qty;
    }
    public void setAllocated_qty(String allocated_qty) {
        this.allocated_qty = allocated_qty;
    }
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

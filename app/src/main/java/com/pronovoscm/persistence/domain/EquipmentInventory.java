package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "eq_inventory")
public class EquipmentInventory {
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "eq_inventory_id")
    Integer eqInventoryId;
    @Property(nameInDb = "eq_region_equipment_id")
    Integer eqRegionEquipentId;
    @Property(nameInDb = "eq_sub_categories_id")
    Integer eqSubCategoryId;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "reason")
    String reason;
    @Property(nameInDb = "owner")
    Integer owner;
    @Property(nameInDb = "manager")
    Integer manager;
    @Property(nameInDb = "quantity")
    Integer quantity;
    @Property(nameInDb = "purchased_from")
    Integer purchasedFrom;
    @Property(nameInDb = "purchase_price")
    Integer purchase_price;
    @Property(nameInDb = "company_id_number")
    String companyIdNumber;
    @Property(nameInDb = "adjustment_reason")
    Integer adjustmentReason;
    @Property(nameInDb = "notes")
    String notes;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "status")
    Integer status;
    @Property(nameInDb = "equipment_status")
    Integer equipmentStatus;
    @Property(nameInDb = "available_quantity")
    Integer availableQuantity;
    @Property(nameInDb = "tenantId")
    Integer tenant_id;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Generated(hash = 650250817)
    public EquipmentInventory(Long id, Integer eqInventoryId,
            Integer eqRegionEquipentId, Integer eqSubCategoryId,
            Integer pjProjectsId, String reason, Integer owner, Integer manager,
            Integer quantity, Integer purchasedFrom, Integer purchase_price,
            String companyIdNumber, Integer adjustmentReason, String notes,
            Date updatedAt, Integer status, Integer equipmentStatus,
            Integer availableQuantity, Integer tenant_id, Date deletedAt,
            Integer usersId) {
        this.id = id;
        this.eqInventoryId = eqInventoryId;
        this.eqRegionEquipentId = eqRegionEquipentId;
        this.eqSubCategoryId = eqSubCategoryId;
        this.pjProjectsId = pjProjectsId;
        this.reason = reason;
        this.owner = owner;
        this.manager = manager;
        this.quantity = quantity;
        this.purchasedFrom = purchasedFrom;
        this.purchase_price = purchase_price;
        this.companyIdNumber = companyIdNumber;
        this.adjustmentReason = adjustmentReason;
        this.notes = notes;
        this.updatedAt = updatedAt;
        this.status = status;
        this.equipmentStatus = equipmentStatus;
        this.availableQuantity = availableQuantity;
        this.tenant_id = tenant_id;
        this.deletedAt = deletedAt;
        this.usersId = usersId;
    }
    @Generated(hash = 1872128597)
    public EquipmentInventory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getEqInventoryId() {
        return this.eqInventoryId;
    }
    public void setEqInventoryId(Integer eqInventoryId) {
        this.eqInventoryId = eqInventoryId;
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
    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }
    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Integer getOwner() {
        return this.owner;
    }
    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    public Integer getManager() {
        return this.manager;
    }
    public void setManager(Integer manager) {
        this.manager = manager;
    }
    public Integer getQuantity() {
        return this.quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getPurchasedFrom() {
        return this.purchasedFrom;
    }
    public void setPurchasedFrom(Integer purchasedFrom) {
        this.purchasedFrom = purchasedFrom;
    }
    public Integer getPurchase_price() {
        return this.purchase_price;
    }
    public void setPurchase_price(Integer purchase_price) {
        this.purchase_price = purchase_price;
    }
    public String getCompanyIdNumber() {
        return this.companyIdNumber;
    }
    public void setCompanyIdNumber(String companyIdNumber) {
        this.companyIdNumber = companyIdNumber;
    }
    public Integer getAdjustmentReason() {
        return this.adjustmentReason;
    }
    public void setAdjustmentReason(Integer adjustmentReason) {
        this.adjustmentReason = adjustmentReason;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getEquipmentStatus() {
        return this.equipmentStatus;
    }
    public void setEquipmentStatus(Integer equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }
    public Integer getAvailableQuantity() {
        return this.availableQuantity;
    }
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
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
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
}

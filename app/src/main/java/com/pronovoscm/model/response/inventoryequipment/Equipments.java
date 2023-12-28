package com.pronovoscm.model.response.inventoryequipment;

import com.google.gson.annotations.SerializedName;

public class Equipments {

    @SerializedName("allocated_qty")
    private String allocatedQty;
    @SerializedName("allocated")
    private int allocated;
    @SerializedName("forecasted")
    private int forecasted;
    @SerializedName("notes")
    private String notes;
    @SerializedName("model")
    private String model;
    @SerializedName("picture")
    private String picture;
    @SerializedName("manufacturer")
    private String manufacturer;
    @SerializedName("regions_id")
    private int regionsId;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("type")
    private String type;
    @SerializedName("items_per_unit")
    private String itemsPerUnit;
    @SerializedName("upc")
    private String upc;
    @SerializedName("weight")
    private String weight;
    @SerializedName("name")
    private String name;
    @SerializedName("eq_sub_categories_id")
    private int eqSubCategoriesId;
    @SerializedName("eq_region_equipment_id")
    private int eqRegionEquipmentId;
    @SerializedName("tenant_id")
    private int tenantId;

    public String getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(String allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public int getAllocated() {
        return allocated;
    }

    public void setAllocated(int allocated) {
        this.allocated = allocated;
    }

    public int getForecasted() {
        return forecasted;
    }

    public void setForecasted(int forecasted) {
        this.forecasted = forecasted;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getRegionsId() {
        return regionsId;
    }

    public void setRegionsId(int regionsId) {
        this.regionsId = regionsId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemsPerUnit() {
        return itemsPerUnit;
    }

    public void setItemsPerUnit(String itemsPerUnit) {
        this.itemsPerUnit = itemsPerUnit;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEqSubCategoriesId() {
        return eqSubCategoriesId;
    }

    public void setEqSubCategoriesId(int eqSubCategoriesId) {
        this.eqSubCategoriesId = eqSubCategoriesId;
    }

    public int getEqRegionEquipmentId() {
        return eqRegionEquipmentId;
    }

    public void setEqRegionEquipmentId(int eqRegionEquipmentId) {
        this.eqRegionEquipmentId = eqRegionEquipmentId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}

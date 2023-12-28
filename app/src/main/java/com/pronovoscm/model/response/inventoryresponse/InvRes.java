package com.pronovoscm.model.response.inventoryresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvRes {
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data {
        @SerializedName("responseMsg")
        private String responsemsg;
        @SerializedName("responseCode")
        private int responsecode;
        @SerializedName("inventory")
        private List<Inventory> inventory;

        public String getResponsemsg() {
            return responsemsg;
        }

        public void setResponsemsg(String responsemsg) {
            this.responsemsg = responsemsg;
        }

        public int getResponsecode() {
            return responsecode;
        }

        public void setResponsecode(int responsecode) {
            this.responsecode = responsecode;
        }

        public List<Inventory> getInventory() {
            return inventory;
        }

        public void setInventory(List<Inventory> inventory) {
            this.inventory = inventory;
        }
    }

    public  class Inventory {
        @SerializedName("tenant_id")
        private int tenantId;
        @SerializedName("equipment_status")
        private int equipmentStatus;
        @SerializedName("status")
        private int status;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("notes")
        private String notes;
        @SerializedName("adjustment_reason")
        private int adjustmentReason;
        @SerializedName("company_id_number")
        private String companyIdNumber;
       @SerializedName("deleted_at")
        private String deletedAt;
        @SerializedName("purchase_price")
        private int purchasePrice;
        @SerializedName("purchased_from")
        private int purchasedFrom;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("pj_projects_id")
        private int pjProjectsId;
        @SerializedName("eq_region_equipment_id")
        private int eqRegionEquipmentId;
        @SerializedName("eq_sub_categories_id")
        private int eqSubCategoriesId;
        @SerializedName("eq_inventory_id")
        private int eqInventoryId;

        public String getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
        }

        public int getTenantId() {
            return tenantId;
        }

        public void setTenantId(int tenantId) {
            this.tenantId = tenantId;
        }

        public int getEquipmentStatus() {
            return equipmentStatus;
        }

        public void setEquipmentStatus(int equipmentStatus) {
            this.equipmentStatus = equipmentStatus;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public int getAdjustmentReason() {
            return adjustmentReason;
        }

        public void setAdjustmentReason(int adjustmentReason) {
            this.adjustmentReason = adjustmentReason;
        }

        public String getCompanyIdNumber() {
            return companyIdNumber;
        }

        public void setCompanyIdNumber(String companyIdNumber) {
            this.companyIdNumber = companyIdNumber;
        }

        public int getPurchasePrice() {
            return purchasePrice;
        }

        public void setPurchasePrice(int purchasePrice) {
            this.purchasePrice = purchasePrice;
        }

        public int getPurchasedFrom() {
            return purchasedFrom;
        }

        public void setPurchasedFrom(int purchasedFrom) {
            this.purchasedFrom = purchasedFrom;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getPjProjectsId() {
            return pjProjectsId;
        }

        public void setPjProjectsId(int pjProjectsId) {
            this.pjProjectsId = pjProjectsId;
        }

        public int getEqRegionEquipmentId() {
            return eqRegionEquipmentId;
        }

        public void setEqRegionEquipmentId(int eqRegionEquipmentId) {
            this.eqRegionEquipmentId = eqRegionEquipmentId;
        }

        public int getEqSubCategoriesId() {
            return eqSubCategoriesId;
        }

        public void setEqSubCategoriesId(int eqSubCategoriesId) {
            this.eqSubCategoriesId = eqSubCategoriesId;
        }

        public int getEqInventoryId() {
            return eqInventoryId;
        }

        public void setEqInventoryId(int eqInventoryId) {
            this.eqInventoryId = eqInventoryId;
        }
    }
}

package com.pronovoscm.model.response.inventorysubcategories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class InventorySubCategoriesResponse {
    
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

    public static class Data {
        
        @SerializedName("responseMsg")
        private String responseMsg;
        
        @SerializedName("responseCode")
        private int responseCode;
        
        @SerializedName("subcategories")
        private List<Subcategories> subcategories;

        public String getResponseMsg() {
            return responseMsg;
        }

        public void setResponseMsg(String responseMsg) {
            this.responseMsg = responseMsg;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public List<Subcategories> getSubcategories() {
            return subcategories;
        }

        public void setSubcategories(List<Subcategories> subcategories) {
            this.subcategories = subcategories;
        }
    }

    public static class Subcategories {

        @SerializedName("name")
        private String name;

        @SerializedName("eq_categories_id")
        private int eq_categories_id;

        @SerializedName("eq_sub_categories_id")
        private int eq_sub_categories_id;

        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("deleted_at")
        private String deletedAt;

        public String getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
        }

        @SerializedName("updated_at")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getTenantId() {
            return tenantId;
        }

        public void setTenantId(int tenantId) {
            this.tenantId = tenantId;
        }

        @SerializedName("tenant_id")
        private int tenantId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getEq_categories_id() {
            return eq_categories_id;
        }

        public void setEq_categories_id(int eq_categories_id) {
            this.eq_categories_id = eq_categories_id;
        }

        public int getEq_sub_categories_id() {
            return eq_sub_categories_id;
        }

        public void setEq_sub_categories_id(int eq_sub_categories_id) {
            this.eq_sub_categories_id = eq_sub_categories_id;
        }
    }
}

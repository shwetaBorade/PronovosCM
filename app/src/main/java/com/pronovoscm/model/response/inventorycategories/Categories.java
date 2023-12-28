package com.pronovoscm.model.response.inventorycategories;

import com.google.gson.annotations.SerializedName;

public class Categories {
    @SerializedName("tenant_id")
    private int tenant_id;
    @SerializedName("allocation_uom")
    private String allocation_uom;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("name")
    private String name;
    @SerializedName("eq_categories_id")
    private int eq_categories_id;

    public int getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getAllocation_uom() {
        return allocation_uom;
    }

    public void setAllocation_uom(String allocation_uom) {
        this.allocation_uom = allocation_uom;
    }

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
}

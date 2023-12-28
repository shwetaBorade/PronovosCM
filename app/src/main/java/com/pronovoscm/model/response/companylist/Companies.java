package com.pronovoscm.model.response.companylist;

import com.google.gson.annotations.SerializedName;

public class Companies {
    @SerializedName("selected")
    private String selected;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("company_id")
    private int companyId;
    @SerializedName("is_deleted")
    private int isDeleted;

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
}

package com.pronovoscm.model.response.projectdata;

import com.google.gson.annotations.SerializedName;

public class Companies {
    @SerializedName("is_deleted")
    private int is_deleted;
    @SerializedName("selected")
    private boolean selected;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("company_id")
    private int company_id;

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
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

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }
}

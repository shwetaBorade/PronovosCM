package com.pronovoscm.model.response.rfi.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiContact {
    @SerializedName("pj_rfi_contact_list_id")
    @Expose
    private Integer pjRfiContactListId;
    @SerializedName("pj_rfi_id")
    @Expose
    private Integer pjRfiId;
    @SerializedName("contact_list")
    @Expose
    private String contactList;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("default_type")
    @Expose
    private Integer defaultType;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getPjRfiContactListId() {
        return pjRfiContactListId;
    }

    public void setPjRfiContactListId(Integer pjRfiContactListId) {
        this.pjRfiContactListId = pjRfiContactListId;
    }

    public Integer getPjRfiId() {
        return pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public String getContactList() {
        return contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Integer defaultType) {
        this.defaultType = defaultType;
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

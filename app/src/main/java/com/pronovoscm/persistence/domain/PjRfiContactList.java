package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

@Entity(nameInDb = "pj_rfi_contact_list")
public class PjRfiContactList implements Serializable {


    public static final long serialVersionUID = 787899978;
    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "pj_rfi_contact_list_id")
    public Integer pjRfiContactListId;
    @Property(nameInDb = "pj_rfi_id")
    public Integer pjRfiId;

    @Property(nameInDb = "contact_list")
    public String contactList;
    @Property(nameInDb = "name")
    public String name;
    @Property(nameInDb = "email")
    public String email;

    @Property(nameInDb = "default_type")
    public Integer defaultType;

    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;

    @Generated(hash = 1360710111)
    public PjRfiContactList(Long id, Integer pjRfiContactListId, Integer pjRfiId,
                            String contactList, String name, String email, Integer defaultType,
                            Date createdAt, Date updatedAt) {
        this.id = id;
        this.pjRfiContactListId = pjRfiContactListId;
        this.pjRfiId = pjRfiId;
        this.contactList = contactList;
        this.name = name;
        this.email = email;
        this.defaultType = defaultType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Generated(hash = 1194397547)
    public PjRfiContactList() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPjRfiContactListId() {
        return this.pjRfiContactListId;
    }

    public void setPjRfiContactListId(Integer pjRfiContactListId) {
        this.pjRfiContactListId = pjRfiContactListId;
    }

    public Integer getPjRfiId() {
        return this.pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public String getContactList() {
        return this.contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    public Integer getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(Integer defaultType) {
        this.defaultType = defaultType;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "PjRfiContactList{" +
                "pjRfiContactListId=" + pjRfiContactListId +
                ", pjRfiId=" + pjRfiId +
                ", contactList='" + contactList + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

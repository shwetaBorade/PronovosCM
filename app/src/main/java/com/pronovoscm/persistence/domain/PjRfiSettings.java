package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_rfi_settings")
public class PjRfiSettings {

    @Id(autoincrement = true)
    public Long id;


    @Property(nameInDb = "pj_rfi_settings_id")
    public Integer pjRfiSettingsId;

    @Property(nameInDb = "default_type")
    public String defaultType;
    @Property(nameInDb = "default_spec_section")
    public String defaultSpecSection;
    @Property(nameInDb = "type")
    public String type;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "contacts_id")
    public Integer contactsId;

    @Property(nameInDb = "created_at")
    public Date createdAt;

    @Property(nameInDb = "updated_at")
    public Date updatedAt;

    @Property(nameInDb = "response_days")
    public Integer responseDays;

    @Generated(hash = 1983904725)
    public PjRfiSettings(Long id, Integer pjRfiSettingsId, String defaultType,
                         String defaultSpecSection, String type, Integer pjProjectsId,
                         Integer contactsId, Date createdAt, Date updatedAt,
                         Integer responseDays) {
        this.id = id;
        this.pjRfiSettingsId = pjRfiSettingsId;
        this.defaultType = defaultType;
        this.defaultSpecSection = defaultSpecSection;
        this.type = type;
        this.pjProjectsId = pjProjectsId;
        this.contactsId = contactsId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.responseDays = responseDays;
    }

    @Generated(hash = 1367165015)
    public PjRfiSettings() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPjRfiSettingsId() {
        return this.pjRfiSettingsId;
    }

    public void setPjRfiSettingsId(Integer pjRfiSettingsId) {
        this.pjRfiSettingsId = pjRfiSettingsId;
    }

    public String getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getDefaultSpecSection() {
        return this.defaultSpecSection;
    }

    public void setDefaultSpecSection(String defaultSpecSection) {
        this.defaultSpecSection = defaultSpecSection;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getContactsId() {
        return this.contactsId;
    }

    public void setContactsId(Integer contactsId) {
        this.contactsId = contactsId;
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

    public Integer getResponseDays() {
        return this.responseDays;
    }

    public void setResponseDays(Integer responseDays) {
        this.responseDays = responseDays;
    }


}

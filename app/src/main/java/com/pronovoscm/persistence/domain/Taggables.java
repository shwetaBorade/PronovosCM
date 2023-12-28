package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "Taggables")
public class Taggables {
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "tag_id")
    Integer tagId;
    @Property(nameInDb = "tag_name")
    String tagName;
    @Property(nameInDb = "taggable_id")
    Integer taggableId;
    @Property(nameInDb = "taggable_id_mobile")
    Long taggableIdMobile;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "user_id")
    Integer userId;

    @Generated(hash = 1933083398)
    public Taggables(Date createdAt, Integer tagId, String tagName,
                     Integer taggableId, Long taggableIdMobile, Date updatedAt,
                     Integer userId) {
        this.createdAt = createdAt;
        this.tagId = tagId;
        this.tagName = tagName;
        this.taggableId = taggableId;
        this.taggableIdMobile = taggableIdMobile;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    @Generated(hash = 734856489)
    public Taggables() {
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTagId() {
        return this.tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getTaggableId() {
        return this.taggableId;
    }

    public void setTaggableId(Integer taggableId) {
        this.taggableId = taggableId;
    }

    public Long getTaggableIdMobile() {
        return this.taggableIdMobile;
    }

    public void setTaggableIdMobile(Long taggableIdMobile) {
        this.taggableIdMobile = taggableIdMobile;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


}

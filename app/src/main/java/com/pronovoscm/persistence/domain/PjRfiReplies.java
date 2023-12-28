package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_rfi_replies")
public class PjRfiReplies {

    @Id(autoincrement = true)
    public Long id;
    @Property(nameInDb = "pj_rfi_id")
    public Integer pjRfiId;
    @Property(nameInDb = "pj_rfi_replies_id")
    public Integer pjRfiRepliesId;
    @Property(nameInDb = "user_id")
    public Integer userId;


    @Property(nameInDb = "rfi_replies")
    public String rfiReplies;
    @Property(nameInDb = "username")
    public String username;

    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "is_official_response")
    public Integer isOfficialResponse;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;

    @Generated(hash = 1120767112)
    public PjRfiReplies(Long id, Integer pjRfiId, Integer pjRfiRepliesId,
                        Integer userId, String rfiReplies, String username, Date createdAt,
                        Date updatedAt, Integer isOfficialResponse, Date deletedAt) {
        this.id = id;
        this.pjRfiId = pjRfiId;
        this.pjRfiRepliesId = pjRfiRepliesId;
        this.userId = userId;
        this.rfiReplies = rfiReplies;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isOfficialResponse = isOfficialResponse;
        this.deletedAt = deletedAt;
    }

    @Generated(hash = 575403238)
    public PjRfiReplies() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPjRfiId() {
        return this.pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public Integer getPjRfiRepliesId() {
        return this.pjRfiRepliesId;
    }

    public void setPjRfiRepliesId(Integer pjRfiRepliesId) {
        this.pjRfiRepliesId = pjRfiRepliesId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRfiReplies() {
        return this.rfiReplies;
    }

    public void setRfiReplies(String rfiReplies) {
        this.rfiReplies = rfiReplies;
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

    public Integer getIsOfficialResponse() {
        return this.isOfficialResponse;
    }

    public void setIsOfficialResponse(Integer isOfficialResponse) {
        this.isOfficialResponse = isOfficialResponse;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

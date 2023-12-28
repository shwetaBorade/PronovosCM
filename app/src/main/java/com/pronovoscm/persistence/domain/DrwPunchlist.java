package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "drw_punchlist")
public class DrwPunchlist {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "drw_drawing_id")
    private Long drawingId;

    @Property(nameInDb = "punch_list_id")
    private Integer punchlistId;

    @Property(nameInDb = "punch_list_id_mobile")
    private Long punchlistIdMobile;

    @Property(nameInDb = "created_at")
    private Date createdAt;

    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;

    @Property(nameInDb = "pj_projects_id")
    private Integer pjProjectsId;

    @Property(nameInDb = "users_id")
    private Integer userId;


    @Generated(hash = 1236891804)
    public DrwPunchlist() {
    }

    @Generated(hash = 1514563527)
    public DrwPunchlist(Long id, Long drawingId, Integer punchlistId,
                        Long punchlistIdMobile, Date createdAt, Date updatedAt, Date deletedAt,
                        Integer pjProjectsId, Integer userId) {
        this.id = id;
        this.drawingId = drawingId;
        this.punchlistId = punchlistId;
        this.punchlistIdMobile = punchlistIdMobile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.pjProjectsId = pjProjectsId;
        this.userId = userId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDrawingId() {
        return this.drawingId;
    }

    public void setDrawingId(Long drawingId) {
        this.drawingId = drawingId;
    }

    public Integer getPunchlistId() {
        return this.punchlistId;
    }

    public void setPunchlistId(Integer punchlistId) {
        this.punchlistId = punchlistId;
    }

    public Long getPunchlistIdMobile() {
        return this.punchlistIdMobile;
    }

    public void setPunchlistIdMobile(Long punchlistIdMobile) {
        this.punchlistIdMobile = punchlistIdMobile;
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

    public void setUpdatedAt(Date UpdatedAt) {
        this.updatedAt = UpdatedAt;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

  
}

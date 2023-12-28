package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "Regions")
public class RegionsTable {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "updated_at")
    private Date updated_at;
    @Property(nameInDb = "created_at")
    private Date created_at;
    @Property(nameInDb = "active")
    private boolean active;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "regions_id")
    private Integer regions_id;
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    @Generated(hash = 849326685)
    public RegionsTable(Integer usersId, Date updated_at, Date created_at,
            boolean active, String name, Integer regions_id, Long id) {
        this.usersId = usersId;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.active = active;
        this.name = name;
        this.regions_id = regions_id;
        this.id = id;
    }
    @Generated(hash = 310442331)
    public RegionsTable() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Date getUpdated_at() {
        return this.updated_at;
    }
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
    public Date getCreated_at() {
        return this.created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public boolean getActive() {
        return this.active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getRegions_id() {
        return this.regions_id;
    }
    public void setRegions_id(Integer regions_id) {
        this.regions_id = regions_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

 }

package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_transaction_log_mobile")
public class TransactionLogMobile {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "module")
    Integer module;
    @Property(nameInDb = "status")
    Integer status;
    @Property(nameInDb = "mobile_id")
    Long mobileId;
    @Property(nameInDb = "server_id")
    Long serverId;
    @Id(autoincrement = true)
    @Index(unique = true)
    @Property(nameInDb = "sync_id")
    Long syncId;
    @Property(nameInDb = "create_date")
    Date createDate;
    @Generated(hash = 1790174105)
    public TransactionLogMobile(Integer usersId, Integer module, Integer status,
            Long mobileId, Long serverId, Long syncId, Date createDate) {
        this.usersId = usersId;
        this.module = module;
        this.status = status;
        this.mobileId = mobileId;
        this.serverId = serverId;
        this.syncId = syncId;
        this.createDate = createDate;
    }
    @Generated(hash = 1792721887)
    public TransactionLogMobile() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getModule() {
        return this.module;
    }
    public void setModule(Integer module) {
        this.module = module;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Long getMobileId() {
        return this.mobileId;
    }
    public void setMobileId(Long mobileId) {
        this.mobileId = mobileId;
    }
    public Long getServerId() {
        return this.serverId;
    }
    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }
    public Long getSyncId() {
        return this.syncId;
    }
    public void setSyncId(Long syncId) {
        this.syncId = syncId;
    }
    public Date getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}

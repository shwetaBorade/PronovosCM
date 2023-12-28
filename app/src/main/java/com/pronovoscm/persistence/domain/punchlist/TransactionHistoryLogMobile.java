package com.pronovoscm.persistence.domain.punchlist;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "transaction_history_log_mobile")
public class TransactionHistoryLogMobile {
    @Property(nameInDb = "punchlist_sync_id")
    Long punchListSyncId;
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

    @Generated(hash = 1948757108)
    public TransactionHistoryLogMobile(Long punchListSyncId, Integer usersId,
            Integer module, Integer status, Long mobileId, Long serverId,
            Long syncId, Date createDate) {
        this.punchListSyncId = punchListSyncId;
        this.usersId = usersId;
        this.module = module;
        this.status = status;
        this.mobileId = mobileId;
        this.serverId = serverId;
        this.syncId = syncId;
        this.createDate = createDate;
    }

    @Generated(hash = 1248390219)
    public TransactionHistoryLogMobile() {
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

    public Long getPunchListSyncId() {
        return punchListSyncId;
    }

    public void setPunchListSyncId(Long punchListSyncId) {
        this.punchListSyncId = punchListSyncId;
    }
}

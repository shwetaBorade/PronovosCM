package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "DrawingXmls")
public class DrawingXmls {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "annotxml")
    String annotxml;
    @Property(nameInDb = "annotdeletexml")
    String annotdeletexml;
    @Property(nameInDb = "drw_drawings_id")
    Integer drwDrawingsId;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Generated(hash = 1373578052)
    public DrawingXmls(Long id, String annotxml, String annotdeletexml,
            Integer drwDrawingsId, Boolean isSync, Integer usersId) {
        this.id = id;
        this.annotxml = annotxml;
        this.annotdeletexml = annotdeletexml;
        this.drwDrawingsId = drwDrawingsId;
        this.isSync = isSync;
        this.usersId = usersId;
    }
    @Generated(hash = 1615690146)
    public DrawingXmls() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAnnotxml() {
        return this.annotxml;
    }
    public void setAnnotxml(String annotxml) {
        this.annotxml = annotxml;
    }
    public String getAnnotdeletexml() {
        return this.annotdeletexml;
    }
    public void setAnnotdeletexml(String annotdeletexml) {
        this.annotdeletexml = annotdeletexml;
    }
    public Integer getDrwDrawingsId() {
        return this.drwDrawingsId;
    }
    public void setDrwDrawingsId(Integer drwDrawingsId) {
        this.drwDrawingsId = drwDrawingsId;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

}

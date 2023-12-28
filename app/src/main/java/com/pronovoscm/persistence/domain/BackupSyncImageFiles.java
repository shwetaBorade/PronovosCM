package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "backupSync")
public class BackupSyncImageFiles {
    @Property(nameInDb = "location")
    String location;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "isSync")
    Boolean isSync;
    @Property(nameInDb = "type")
    String type;
    @Id(autoincrement = true)
    private Long id;


    @Generated(hash = 1366663125)
    public BackupSyncImageFiles(String location, String name, Boolean isSync,
                                String type, Long id) {
        this.location = location;
        this.name = name;
        this.isSync = isSync;
        this.type = type;
        this.id = id;
    }

    @Generated(hash = 495412761)
    public BackupSyncImageFiles() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public String toString() {
        return "\n BackupSyncImages{" +
                "location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", type =" + type +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "form_image")
public class FormImage {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "image_name")
    private String imageName;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Generated(hash = 1844629121)
    public FormImage(Long id, String imageName, Boolean isSync) {
        this.id = id;
        this.imageName = imageName;
        this.isSync = isSync;
    }
    @Generated(hash = 1722452236)
    public FormImage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getImageName() {
        return this.imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
}

package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "form_assets")
public class FormAssets {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "file_path")
    private String filePath;
    @Property(nameInDb = "file_type")
    private String fileType;
    @Property(nameInDb = "file_name")
    private String fileName;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Generated(hash = 1107365325)
    public FormAssets(Long id, String filePath, String fileType, String fileName,
            Date UpdatedAt) {
        this.id = id;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileName = fileName;
        this.UpdatedAt = UpdatedAt;
    }
    @Generated(hash = 1918559535)
    public FormAssets() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileType() {
        return this.fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Date getUpdatedAt() {
        return this.UpdatedAt;
    }
    public void setUpdatedAt(Date UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }

}

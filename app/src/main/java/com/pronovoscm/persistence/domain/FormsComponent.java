package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "form_component")
public class FormsComponent {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Property(nameInDb = "created_at")
    private Date CreatedAt;
    @Property(nameInDb = "forms_id")
    private Integer formsId;
    @Property(nameInDb = "forms_components")
    private String formsComponents;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Generated(hash = 501062368)
    public FormsComponent(Long id, Date UpdatedAt, Date CreatedAt, Integer formsId,
            String formsComponents, Date deletedAt) {
        this.id = id;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.formsId = formsId;
        this.formsComponents = formsComponents;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 74040939)
    public FormsComponent() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getUpdatedAt() {
        return this.UpdatedAt;
    }
    public void setUpdatedAt(Date UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }
    public Date getCreatedAt() {
        return this.CreatedAt;
    }
    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }
    public Integer getFormsId() {
        return this.formsId;
    }
    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }
    public String getFormsComponents() {
        return this.formsComponents;
    }
    public void setFormsComponents(String formsComponents) {
        this.formsComponents = formsComponents;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    
}

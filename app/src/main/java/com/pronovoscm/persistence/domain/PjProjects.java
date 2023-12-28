package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "pj_projects_mobile")
public class PjProjects {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Property(nameInDb = "created_at")
    private Date CreatedAt;
    @Property(nameInDb = "is_archived")
    private Integer IsArchived;
    @Property(nameInDb = "zip")
    private String Zip;
    @Property(nameInDb = "city")
    private String City;
    @Property(nameInDb = "state")
    private String State;
    @Property(nameInDb = "address")
    private String Address;
    @Property(nameInDb = "project_number")
    private String ProjectNumber;
    @Property(nameInDb = "name")
    private String Name;
    @Property(nameInDb = "showcase_photo")
    private String showcasePhoto;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "region_id")
    private Integer region_id;
    @Generated(hash = 1662698033)
    public PjProjects(Integer usersId, Long id, Date UpdatedAt, Date CreatedAt,
            Integer IsArchived, String Zip, String City, String State,
            String Address, String ProjectNumber, String Name, String showcasePhoto,
            Integer PjProjectsId, Integer region_id) {
        this.usersId = usersId;
        this.id = id;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.IsArchived = IsArchived;
        this.Zip = Zip;
        this.City = City;
        this.State = State;
        this.Address = Address;
        this.ProjectNumber = ProjectNumber;
        this.Name = Name;
        this.showcasePhoto = showcasePhoto;
        this.PjProjectsId = PjProjectsId;
        this.region_id = region_id;
    }
    @Generated(hash = 1015885005)
    public PjProjects() {
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
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
    public Integer getIsArchived() {
        return this.IsArchived;
    }
    public void setIsArchived(Integer IsArchived) {
        this.IsArchived = IsArchived;
    }
    public String getZip() {
        return this.Zip;
    }
    public void setZip(String Zip) {
        this.Zip = Zip;
    }
    public String getCity() {
        return this.City;
    }
    public void setCity(String City) {
        this.City = City;
    }
    public String getState() {
        return this.State;
    }
    public void setState(String State) {
        this.State = State;
    }
    public String getAddress() {
        return this.Address;
    }
    public void setAddress(String Address) {
        this.Address = Address;
    }
    public String getProjectNumber() {
        return this.ProjectNumber;
    }
    public void setProjectNumber(String ProjectNumber) {
        this.ProjectNumber = ProjectNumber;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getShowcasePhoto() {
        return this.showcasePhoto;
    }
    public void setShowcasePhoto(String showcasePhoto) {
        this.showcasePhoto = showcasePhoto;
    }
    public Integer getPjProjectsId() {
        return this.PjProjectsId;
    }
    public void setPjProjectsId(Integer PjProjectsId) {
        this.PjProjectsId = PjProjectsId;
    }
    public Integer getRegion_id() {
        return this.region_id;
    }
    public void setRegion_id(Integer region_id) {
        this.region_id = region_id;
    }
    
}

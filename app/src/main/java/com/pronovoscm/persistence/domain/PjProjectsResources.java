package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "pj_projects_resources")
public class PjProjectsResources {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "project_resources")
    private String ProjectsResources;
    @Generated(hash = 595454514)
    public PjProjectsResources(Integer usersId, Long id, Integer PjProjectsId,
            String ProjectsResources) {
        this.usersId = usersId;
        this.id = id;
        this.PjProjectsId = PjProjectsId;
        this.ProjectsResources = ProjectsResources;
    }
    @Generated(hash = 2120270388)
    public PjProjectsResources() {
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
    public Integer getPjProjectsId() {
        return this.PjProjectsId;
    }
    public void setPjProjectsId(Integer PjProjectsId) {
        this.PjProjectsId = PjProjectsId;
    }
    public String getProjectsResources() {
        return this.ProjectsResources;
    }
    public void setProjectsResources(String ProjectsResources) {
        this.ProjectsResources = ProjectsResources;
    }

  
}

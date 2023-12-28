package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "pj_projects_subcontractors")
public class PjProjectsSubcontractors {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "project_subcontractors")
    private String ProjectsSubcontractors;
    @Generated(hash = 811855190)
    public PjProjectsSubcontractors(Integer usersId, Long id, Integer PjProjectsId,
            String ProjectsSubcontractors) {
        this.usersId = usersId;
        this.id = id;
        this.PjProjectsId = PjProjectsId;
        this.ProjectsSubcontractors = ProjectsSubcontractors;
    }
    @Generated(hash = 2019216275)
    public PjProjectsSubcontractors() {
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
    public String getProjectsSubcontractors() {
        return this.ProjectsSubcontractors;
    }
    public void setProjectsSubcontractors(String ProjectsSubcontractors) {
        this.ProjectsSubcontractors = ProjectsSubcontractors;
    }


}

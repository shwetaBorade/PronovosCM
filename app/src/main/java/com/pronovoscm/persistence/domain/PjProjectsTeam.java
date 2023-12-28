package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "pj_projects_team")
public class PjProjectsTeam {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "project_team")
    private String ProjectTeam;
    @Generated(hash = 225624407)
    public PjProjectsTeam(Integer usersId, Long id, Integer PjProjectsId,
            String ProjectTeam) {
        this.usersId = usersId;
        this.id = id;
        this.PjProjectsId = PjProjectsId;
        this.ProjectTeam = ProjectTeam;
    }
    @Generated(hash = 432074810)
    public PjProjectsTeam() {
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
    public String getProjectTeam() {
        return this.ProjectTeam;
    }
    public void setProjectTeam(String ProjectTeam) {
        this.ProjectTeam = ProjectTeam;
    }


}

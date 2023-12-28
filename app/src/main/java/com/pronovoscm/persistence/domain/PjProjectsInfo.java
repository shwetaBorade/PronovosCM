package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "pj_projects_info")
public class PjProjectsInfo {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "project_info")
    private String ProjectsInfo;

    @Generated(hash = 1511832028)
    public PjProjectsInfo(Integer usersId, Long id, Integer PjProjectsId,
                          String ProjectsInfo) {
        this.usersId = usersId;
        this.id = id;
        this.PjProjectsId = PjProjectsId;
        this.ProjectsInfo = ProjectsInfo;
    }

    @Generated(hash = 945907201)
    public PjProjectsInfo() {
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

    public String getProjectsInfo() {
        return this.ProjectsInfo;
    }

    public void setProjectsInfo(String ProjectsInfo) {
        this.ProjectsInfo = ProjectsInfo;
    }


}

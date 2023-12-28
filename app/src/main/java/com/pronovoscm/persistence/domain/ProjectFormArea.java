package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "project_form_areas")
public class ProjectFormArea {
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "project_form_areas")
    public String projectFormAreas;
    @Id
    @Property(nameInDb = "pj_projects_id")
    private Long pjProjectsId;

    @Generated(hash = 1287964530)
    public ProjectFormArea(Date updatedAt, String projectFormAreas,
                           Long pjProjectsId) {
        this.updatedAt = updatedAt;
        this.projectFormAreas = projectFormAreas;
        this.pjProjectsId = pjProjectsId;
    }

    @Generated(hash = 833357879)
    public ProjectFormArea() {
    }

    public Long getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Long pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getProjectFormAreas() {
        return projectFormAreas;
    }

    public void setProjectFormAreas(String projectFormAreas) {
        this.projectFormAreas = projectFormAreas;
    }
}

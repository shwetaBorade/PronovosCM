package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "forms_name")
public class FormsName {

    @Property(nameInDb = "original_forms_id")
    public Integer originalFormsId;
    @Property(nameInDb = "form_name")
    public String formName;
    @Property(nameInDb = "revision_number")
    public Integer revisionNumber;
    @Id
    @Property(nameInDb = "forms_id")
    private Long formsId;
    @Property(nameInDb = "pj_projects_id")
    private Integer pjProjectsId;


    @Generated(hash = 1908337392)
    public FormsName() {
    }


    @Generated(hash = 1279565049)
    public FormsName(Integer originalFormsId, String formName,
                     Integer revisionNumber, Long formsId, Integer pjProjectsId) {
        this.originalFormsId = originalFormsId;
        this.formName = formName;
        this.revisionNumber = revisionNumber;
        this.formsId = formsId;
        this.pjProjectsId = pjProjectsId;
    }


    public Integer getOriginalFormsId() {
        return originalFormsId;
    }

    public void setOriginalFormsId(Integer originalFormsId) {
        this.originalFormsId = originalFormsId;
    }

    public Long getFormsId() {
        return formsId;
    }

    public void setFormsId(Long formsId) {
        this.formsId = formsId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }


    @Override
    public String toString() {
        return "FormsName{" +
                "originalFormsId=" + originalFormsId +
                ", formsId=" + formsId +
                ", formName='" + formName + '\'' +
                ", pjProjectsId=" + pjProjectsId +
                ", revisionNumber=" + revisionNumber +
                "}\n";
    }
}

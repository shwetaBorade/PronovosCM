package com.pronovoscm.model.response.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormRevision {
    @SerializedName("forms_id")
    @Expose
    private Integer formsId;
    @SerializedName("original_forms_id")
    @Expose
    private Integer originalFormsId;
    @SerializedName("form_name")
    @Expose
    private String formName;
    @SerializedName("revision_number")
    @Expose
    private Integer revisionNumber;

    public Integer getFormsId() {
        return formsId;
    }

    public void setFormsId(Integer formsId) {
        this.formsId = formsId;
    }

    public Integer getOriginalFormsId() {
        return originalFormsId;
    }

    public void setOriginalFormsId(Integer originalFormsId) {
        this.originalFormsId = originalFormsId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }
}

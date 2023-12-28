package com.pronovoscm.model;

import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.UserForms;

public class CalendarFormObject {
    private FormsSchedule formsSchedule;
    private UserForms userForms;
    private Forms forms;
    int revisionNumber;
    int originalFormId;
    private String formName;


    public CalendarFormObject(FormsSchedule formsSchedule, Forms forms, int revisionNumber, int originalFormID, String formName) {
        this.formsSchedule = formsSchedule;
        this.forms = forms;
        this.revisionNumber = revisionNumber;
        this.originalFormId = originalFormID;
        this.formName = formName;
    }

    public CalendarFormObject(UserForms userForms, Forms forms, int revisionNumber, int originalFormID, String formName) {
        this.userForms = userForms;
        this.forms = forms;
        this.revisionNumber = revisionNumber;
        this.originalFormId = originalFormID;
        this.formName = formName;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public int getOriginalFormId() {
        return originalFormId;
    }

    public void setOriginalFormId(int originalFormId) {
        this.originalFormId = originalFormId;
    }

    public CalendarFormObject(FormsSchedule formsSchedule) {
        this.formsSchedule = formsSchedule;
    }

    public CalendarFormObject(UserForms userForms) {
        this.userForms = userForms;
    }

    public FormsSchedule getFormsSchedule() {
        return formsSchedule;
    }

    public void setFormsSchedule(FormsSchedule formsSchedule) {
        this.formsSchedule = formsSchedule;
    }

    public UserForms getUserForms() {
        return userForms;
    }

    public void setUserForms(UserForms userForms) {
        this.userForms = userForms;
    }

    public Forms getForms() {
        return forms;
    }

    public void setForms(Forms forms) {
        this.forms = forms;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}

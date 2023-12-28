package com.pronovoscm.model.request.submitform;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Submission {
    @SerializedName("deleted_images")
    private List<String> deletedImages;
    @SerializedName("cc")
    private List<String> cc;
    @SerializedName("send_email")
    private int sendEmail;
    @SerializedName("to")
    private String to;
    @SerializedName("user_forms_id")
    private long userFormsId;
    @SerializedName("user_form_mobile_id")
    private long userFormMobileId;
    @SerializedName("submitted_data")
    private String submittedData;
    @SerializedName("project")
    private int project;
    @SerializedName("form")
    private int form;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("scheduled_forms_id")
    private Integer scheduleFormId;
    @SerializedName("publish")
    private Integer publish;

    @SerializedName("revision_number")
    private Integer revisionNumber;


    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("form_savedate")
    private String formSaveDate;
    @SerializedName("pj_areas_id")
    private Integer pjAreasId;


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


    public List<String> getDeletedImages() {
        return deletedImages;
    }

    public void setDeletedImages(List<String> deletedImages) {
        this.deletedImages = deletedImages;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public int getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(int sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getUserFormsId() {
        return userFormsId;
    }

    public void setUserFormsId(long userFormsId) {
        this.userFormsId = userFormsId;
    }

    public long getUserFormMobileId() {
        return userFormMobileId;
    }

    public void setUserFormMobileId(long userFormMobileId) {
        this.userFormMobileId = userFormMobileId;
    }

    public String getSubmittedData() {
        return submittedData;
    }

    public void setSubmittedData(String submittedData) {
        this.submittedData = submittedData;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getScheduleFormId() {
        return scheduleFormId;
    }

    public void setScheduleFormId(Integer scheduleFormId) {
        this.scheduleFormId = scheduleFormId;
    }

    public Integer getPublish() {
        return publish;
    }

    public void setPublish(Integer publish) {
        this.publish = publish;
    }

    public String getSaveDate() {
        return formSaveDate;
    }

    public void setSaveDate(String saveDate) {
        this.formSaveDate = saveDate;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getPjAreasId() {
        return pjAreasId;
    }

    public void setPjAreasId(Integer pjAreasId) {
        this.pjAreasId = pjAreasId;
    }
}

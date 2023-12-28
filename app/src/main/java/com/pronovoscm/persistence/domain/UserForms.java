package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "user_forms")
public class UserForms {
    @Property(nameInDb = "created_user_id")
    Integer createdUserId;
    @Property(nameInDb = "updated_user_id")
    Integer updatedUserId;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "updated_at")
    private Date UpdatedAt;
    @Property(nameInDb = "created_at")
    private Date CreatedAt;
    @Property(nameInDb = "due_date")
    private Date dueDate;
    @Property(nameInDb = "updated_by_user_name")
    private String updatedByUserName;
    @Property(nameInDb = "created_by_user_name")
    private String createdByUserName;
    @Property(nameInDb = "pj_projects_id")
    private Integer PjProjectsId;
    @Property(nameInDb = "is_sync")
    private Boolean isSync;
    @Property(nameInDb = "form_id")
    private Integer formId;
    @Property(nameInDb = "tenant_id")
    private Integer tenantId;
    @Property(nameInDb = "publish")
    private Integer publish;
    @Property(nameInDb = "form_submit_id")
    private Long formSubmitId;
    @Property(nameInDb = "form_submit_mobile_id")
    private Long formSubmitMobileId;
    @Property(nameInDb = "deleted_at")
    private Date deletedAt;
    @Property(nameInDb = "date_sent")
    private Date dateSent;
    @Property(nameInDb = "submitted_data")
    private String submittedData;
    @Property(nameInDb = "temp_submitted_data")
    private String tempSubmittedData;
    @Property(nameInDb = "schedule_form_id")
    private Integer scheduleFormId;
    @Property(nameInDb = "deleted_images")
    private String deletedImages;
    @Property(nameInDb = "users_id")
    private Integer usersId;
    @Property(nameInDb = "form_savedate")
    public Date formSaveDate;
    @Property(nameInDb = "revision_number")
    private Integer revisionNumber;
    @Property(nameInDb = "pj_areas_id")
    private Integer pjAreasId;

    @Property(nameInDb = "email_status")
    private Integer emailStatus = 0;

    @Generated(hash = 242410151)
    public UserForms() {
    }

    @Generated(hash = 1154807489)
    public UserForms(Integer createdUserId, Integer updatedUserId, Long id, Date UpdatedAt,
            Date CreatedAt, Date dueDate, String updatedByUserName, String createdByUserName,
            Integer PjProjectsId, Boolean isSync, Integer formId, Integer tenantId,
            Integer publish, Long formSubmitId, Long formSubmitMobileId, Date deletedAt,
            Date dateSent, String submittedData, String tempSubmittedData,
            Integer scheduleFormId, String deletedImages, Integer usersId, Date formSaveDate,
            Integer revisionNumber, Integer pjAreasId, Integer emailStatus) {
        this.createdUserId = createdUserId;
        this.updatedUserId = updatedUserId;
        this.id = id;
        this.UpdatedAt = UpdatedAt;
        this.CreatedAt = CreatedAt;
        this.dueDate = dueDate;
        this.updatedByUserName = updatedByUserName;
        this.createdByUserName = createdByUserName;
        this.PjProjectsId = PjProjectsId;
        this.isSync = isSync;
        this.formId = formId;
        this.tenantId = tenantId;
        this.publish = publish;
        this.formSubmitId = formSubmitId;
        this.formSubmitMobileId = formSubmitMobileId;
        this.deletedAt = deletedAt;
        this.dateSent = dateSent;
        this.submittedData = submittedData;
        this.tempSubmittedData = tempSubmittedData;
        this.scheduleFormId = scheduleFormId;
        this.deletedImages = deletedImages;
        this.usersId = usersId;
        this.formSaveDate = formSaveDate;
        this.revisionNumber = revisionNumber;
        this.pjAreasId = pjAreasId;
        this.emailStatus = emailStatus;
    }

    public Integer getCreatedUserId() {
        return this.createdUserId;
    }
    public void setCreatedUserId(Integer createdUserId) {
        this.createdUserId = createdUserId;
    }
    public Integer getUpdatedUserId() {
        return this.updatedUserId;
    }
    public void setUpdatedUserId(Integer updatedUserId) {
        this.updatedUserId = updatedUserId;
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
    public Date getDueDate() {
        return this.dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    public String getUpdatedByUserName() {
        return this.updatedByUserName;
    }
    public void setUpdatedByUserName(String updatedByUserName) {
        this.updatedByUserName = updatedByUserName;
    }
    public String getCreatedByUserName() {
        return this.createdByUserName;
    }
    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }
    public Integer getPjProjectsId() {
        return this.PjProjectsId;
    }
    public void setPjProjectsId(Integer PjProjectsId) {
        this.PjProjectsId = PjProjectsId;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public Integer getFormId() {
        return this.formId;
    }
    public void setFormId(Integer formId) {
        this.formId = formId;
    }
    public Integer getTenantId() {
        return this.tenantId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    public Integer getPublish() {
        return this.publish;
    }
    public void setPublish(Integer publish) {
        this.publish = publish;
    }
    public Long getFormSubmitId() {
        return this.formSubmitId;
    }
    public void setFormSubmitId(Long formSubmitId) {
        this.formSubmitId = formSubmitId;
    }
    public Long getFormSubmitMobileId() {
        return this.formSubmitMobileId;
    }
    public void setFormSubmitMobileId(Long formSubmitMobileId) {
        this.formSubmitMobileId = formSubmitMobileId;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Date getDateSent() {
        return this.dateSent;
    }
    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
    public String getSubmittedData() {
        return this.submittedData;
    }
    public void setSubmittedData(String submittedData) {
        this.submittedData = submittedData;
    }
    public String getTempSubmittedData() {
        return this.tempSubmittedData;
    }
    public void setTempSubmittedData(String tempSubmittedData) {
        this.tempSubmittedData = tempSubmittedData;
    }
    public Integer getScheduleFormId() {
        return this.scheduleFormId;
    }
    public void setScheduleFormId(Integer scheduleFormId) {
        this.scheduleFormId = scheduleFormId;
    }
    public String getDeletedImages() {
        return this.deletedImages;
    }
    public void setDeletedImages(String deletedImages) {
        this.deletedImages = deletedImages;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public Date getFormSaveDate() {
        return formSaveDate;
    }

    public void setFormSaveDate(Date formSaveDate) {
        this.formSaveDate = formSaveDate;
    }

    @Override
    public String toString() {
        return "UserForms{" +
                " \n formId ======  " + formId +
                " revision_number ======  " + revisionNumber +
                //   "createdUserId=" + createdUserId +
                //   ", updatedUserId=" + updatedUserId +
                //    ", id=" + id +
                //  ", UpdatedAt=" + UpdatedAt +
                // ", CreatedAt=" + CreatedAt +
                //  ", dueDate=" + dueDate +
                ", formSubmitId=" + formSubmitId +
                ", formSubmitMobileId=" + formSubmitMobileId +
                // ", deletedAt=" + deletedAt +
                // ", dateSent=" + dateSent +
                //  ", submittedData='" + submittedData + '\'' +
                // ", tempSubmittedData='" + tempSubmittedData + '\'' +
                ", scheduleFormId=" + scheduleFormId +
                // ", deletedImages='" + deletedImages + '\'' +
                ", usersId=" + usersId +
                //     ", formSaveDate='" + formSaveDate + '\'' +
                "}";
    }

    public Integer getRevisionNumber() {
        return this.revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getPjAreasId() {
        return this.pjAreasId;
    }

    public void setPjAreasId(Integer pjAreasId) {
        this.pjAreasId = pjAreasId;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }
}

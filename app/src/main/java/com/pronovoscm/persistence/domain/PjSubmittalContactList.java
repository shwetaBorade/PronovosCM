package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
@Entity(nameInDb = "Pj_submittal_contact_list")
public class PjSubmittalContactList implements Serializable {

    public static final long serialVersionUID = 536871008;
    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "comments")
    public String comments;
    @Property(nameInDb = "company_name")
    public String companyName;
    @Property(nameInDb = "contact_list")
    public String contactList;
    @Property(nameInDb = "created_at")
    public Date createdAt;
    @Property(nameInDb = "is_sync")
    public Boolean isSync;
    @Property(nameInDb = "pj_projects_id")
    public Integer pjProjectsId;
    @Property(nameInDb = "pj_submittal_contact_list_id")
    public Integer pjSubmittalContactListId;
    @Property(nameInDb = "pj_submittal_mobile_id")
    public Integer pjSubmittalMobileId;
    @Property(nameInDb = "pj_submittals_id")
    public Integer pjSubmittalsId;
    @Property(nameInDb = "response")
    public Integer response;
    @Property(nameInDb = "response_date")
    public Date responseDate;
    @Property(nameInDb = "updated_at")
    public Date updatedAt;
    @Property(nameInDb = "deleted_at")
    public Date deletedAt;
    @Property(nameInDb = "users_id")
    public Integer usersId;
    @Property(nameInDb = "contact_name")
    public String contactName;
    @Property(nameInDb = "sort_order")
    public Integer sortOrder;
    @Property(nameInDb = "pj_submittal_approver_responses_id")
    public Integer pjSubmittalApproverResponsesId;
    @Property(nameInDb = "isCc")
    public boolean isCc;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPjSubmittalApproverResponsesId() {
        return pjSubmittalApproverResponsesId;
    }

    public void setPjSubmittalApproverResponsesId(Integer pjSubmittalApproverResponsesId) {
        this.pjSubmittalApproverResponsesId = pjSubmittalApproverResponsesId;
    }

    public boolean isCc() {
        return isCc;
    }

    public void setCc(boolean cc) {
        isCc = cc;
    }

    @Generated(hash = 981552621)
    public PjSubmittalContactList(Long id, String comments, String companyName,
            String contactList, Date createdAt, Boolean isSync, Integer pjProjectsId,
            Integer pjSubmittalContactListId, Integer pjSubmittalMobileId,
            Integer pjSubmittalsId, Integer response, Date responseDate, Date updatedAt,
            Date deletedAt, Integer usersId, String contactName, Integer sortOrder,
            Integer pjSubmittalApproverResponsesId, boolean isCc) {
        this.id = id;
        this.comments = comments;
        this.companyName = companyName;
        this.contactList = contactList;
        this.createdAt = createdAt;
        this.isSync = isSync;
        this.pjProjectsId = pjProjectsId;
        this.pjSubmittalContactListId = pjSubmittalContactListId;
        this.pjSubmittalMobileId = pjSubmittalMobileId;
        this.pjSubmittalsId = pjSubmittalsId;
        this.response = response;
        this.responseDate = responseDate;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.usersId = usersId;
        this.contactName = contactName;
        this.sortOrder = sortOrder;
        this.pjSubmittalApproverResponsesId = pjSubmittalApproverResponsesId;
        this.isCc = isCc;
    }

    @Generated(hash = 1687377875)
    public PjSubmittalContactList() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactList() {
        return contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public Integer getPjSubmittalContactListId() {
        return pjSubmittalContactListId;
    }

    public void setPjSubmittalContactListId(Integer pjSubmittalContactListId) {
        this.pjSubmittalContactListId = pjSubmittalContactListId;
    }

    public Integer getPjSubmittalMobileId() {
        return pjSubmittalMobileId;
    }

    public void setPjSubmittalMobileId(Integer pjSubmittalMobileId) {
        this.pjSubmittalMobileId = pjSubmittalMobileId;
    }

    public Integer getPjSubmittalsId() {
        return pjSubmittalsId;
    }

    public void setPjSubmittalsId(Integer pjSubmittalsId) {
        this.pjSubmittalsId = pjSubmittalsId;
    }

    public Integer getResponse() {
        return response;
    }

    public void setResponse(Integer response) {
        this.response = response;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public Boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public boolean getIsCc() {
        return this.isCc;
    }

    public void setIsCc(boolean isCc) {
        this.isCc = isCc;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}

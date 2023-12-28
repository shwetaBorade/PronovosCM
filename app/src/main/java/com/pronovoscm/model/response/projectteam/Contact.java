package com.pronovoscm.model.response.projectteam;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact implements Cloneable {
    @SerializedName("company_contacts_list_id")
    @Expose
    private Integer companyContactsListId;
    @SerializedName("contacts_id")
    @Expose
    private Integer contactsId;
    @SerializedName("contact_roles_id")
    @Expose
    private Integer contactRolesId;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;
    @SerializedName("contact_name")
    @Expose
    private String contactName;
    @SerializedName("contact_role_name")
    @Expose
    private String contactRoleName;
    @SerializedName("cellphone")
    @Expose
    private String cellphone;
    @SerializedName("emailaddress")
    @Expose
    private String emailaddress;
    @SerializedName("officelocation")
    @Expose
    private String officelocation;
    @SerializedName("companycontacttypes_id")
    @Expose
    private Integer companycontacttypesId;

    public Integer getCompanyContactsListId() {
        return companyContactsListId;
    }

    public void setCompanyContactsListId(Integer companyContactsListId) {
        this.companyContactsListId = companyContactsListId;
    }

    public void setCompanyContactsListId(int companyContactsListId) {
        this.companyContactsListId = companyContactsListId;
    }

    @NonNull
    @Override
    public Contact clone() throws CloneNotSupportedException {
        Contact c = new Contact();
        c.setCompanyContactsListId(this.companyContactsListId);
        c.setContactsId(this.contactsId);

        c.setPjProjectsId(this.pjProjectsId);
        c.setContactRolesId(this.contactRolesId);

        c.setContactName(this.contactName);
        c.setContactRoleName(this.contactRoleName);

        c.setCellphone(this.cellphone);
        c.setEmailaddress(this.emailaddress);

        c.setOfficelocation(this.officelocation);
        c.setCompanycontacttypesId(this.companycontacttypesId);

        return c;
    }

    public Integer getContactsId() {
        return contactsId;
    }

    public void setContactsId(Integer contactsId) {
        this.contactsId = contactsId;
    }

    public void setContactsId(int contactsId) {
        this.contactsId = contactsId;
    }

    public Integer getContactRolesId() {
        return contactRolesId;
    }

    public void setContactRolesId(Integer contactRolesId) {
        this.contactRolesId = contactRolesId;
    }

    public void setContactRolesId(int contactRolesId) {
        this.contactRolesId = contactRolesId;
    }

    public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactRoleName() {
        return contactRoleName;
    }

    public void setContactRoleName(String contactRoleName) {
        this.contactRoleName = contactRoleName;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public String getOfficelocation() {
        return officelocation;
    }

    public void setOfficelocation(String officelocation) {
        this.officelocation = officelocation;
    }

    public Integer getCompanycontacttypesId() {
        return companycontacttypesId;
    }

    public void setCompanycontacttypesId(Integer companycontacttypesId) {
        this.companycontacttypesId = companycontacttypesId;
    }

    public void setCompanycontacttypesId(int companycontacttypesId) {
        this.companycontacttypesId = companycontacttypesId;
    }
}

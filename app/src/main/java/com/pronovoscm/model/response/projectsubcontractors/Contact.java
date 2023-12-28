package com.pronovoscm.model.response.projectsubcontractors;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("email")
    private String email;
    @SerializedName("projectrole")
    private String projectrole;
    @SerializedName("cellphone")
    private String cellphone;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("project_roles_id")
    private int projectRolesId;
    @SerializedName("contacts_id")
    private int contactsId;
    @SerializedName("pj_subcontractors_id")
    private int pjSubcontractorsId;
    @SerializedName("pj_subcontractor_list_id")
    private int pjSubcontractorListId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProjectrole() {
        return projectrole;
    }

    public void setProjectrole(String projectrole) {
        this.projectrole = projectrole;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getProjectRolesId() {
        return projectRolesId;
    }

    public void setProjectRolesId(int projectRolesId) {
        this.projectRolesId = projectRolesId;
    }

    public int getContactsId() {
        return contactsId;
    }

    public void setContactsId(int contactsId) {
        this.contactsId = contactsId;
    }

    public int getPjSubcontractorsId() {
        return pjSubcontractorsId;
    }

    public void setPjSubcontractorsId(int pjSubcontractorsId) {
        this.pjSubcontractorsId = pjSubcontractorsId;
    }

    public int getPjSubcontractorListId() {
        return pjSubcontractorListId;
    }

    public void setPjSubcontractorListId(int pjSubcontractorListId) {
        this.pjSubcontractorListId = pjSubcontractorListId;
    }
}

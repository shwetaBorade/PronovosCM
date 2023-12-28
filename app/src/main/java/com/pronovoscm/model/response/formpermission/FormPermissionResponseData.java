package com.pronovoscm.model.response.formpermission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormPermissionResponseData {
    @SerializedName("form_permissions")
    @Expose
    private List<FormPermissions> formPermissions = null;

    public List<FormPermissions> getFormPermissions() {
        return formPermissions;
    }

    public void setFormPermissions(List<FormPermissions> formPermissions) {
        this.formPermissions = formPermissions;
    }

}

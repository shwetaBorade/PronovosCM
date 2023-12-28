package com.pronovoscm.model.request.formDelete;

import com.google.gson.annotations.SerializedName;

public class UserFormDeleteRequest {

    @SerializedName("user_form_id")
    private int userFormId;

    public int getUserFormId() {
        return userFormId;
    }

    public void setUserFormId(int userFormId) {
        this.userFormId = userFormId;
    }
}

package com.pronovoscm.model.response.emailassignee;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.materialchips.model.ChipInterface;

public class AssigneeList implements ChipInterface {
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("users_id")
    private int usersId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    @Override
    public Object getId() {
        return this;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return email;
    }
}

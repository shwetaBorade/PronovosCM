package com.pronovoscm.model.response.cclist;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.pronovoscm.materialchips.model.ChipInterface;

import java.io.Serializable;
import java.util.Objects;

public class Cclist implements ChipInterface, Serializable {
    @SerializedName("type")
    private String type;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("users_id")
    private int usersId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
    public boolean equals(Object o) {
        Cclist cclist = (Cclist) o;
        return usersId == cclist.usersId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(usersId);
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

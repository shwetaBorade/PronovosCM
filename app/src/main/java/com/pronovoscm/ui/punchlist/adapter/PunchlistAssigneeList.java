package com.pronovoscm.ui.punchlist.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.pronovoscm.materialchips.model.ChipInterface;
import com.pronovoscm.model.response.cclist.Cclist;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Objects;

public class PunchlistAssigneeList implements ChipInterface, Serializable {
    static final long serialVersionUID = 1L;
    Integer usersId;
    Integer userId;
    String name;
    Boolean active;
    Integer pjProjectsId;

    Boolean defaultAssignee;
    Boolean defaultCC;



    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getUserId() {
        return this.userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getActive() {
        return this.active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }
    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    @Override
    public boolean equals(Object o) {
        PunchlistAssigneeList punchlistAssigneeList = (PunchlistAssigneeList) o;
        return usersId == punchlistAssigneeList.usersId;
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
        return "";
    }

    public Boolean getDefaultAssignee() {
        return defaultAssignee;
    }

    public void setDefaultAssignee(Boolean defaultAssignee) {
        this.defaultAssignee = defaultAssignee;
    }

    public Boolean getDefaultCC() {
        return defaultCC;
    }

    public void setDefaultCC(Boolean defaultCC) {
        this.defaultCC = defaultCC;
    }
}

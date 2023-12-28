package com.pronovoscm.persistence.domain;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.pronovoscm.materialchips.model.ChipInterface;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity(nameInDb = "PunchlistAssignee")
public class PunchlistAssignee implements ChipInterface, Serializable,Parcelable {
    static final long serialVersionUID = 2L;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "user_id")
    Integer userId;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "active")
    Boolean active;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;

    @Property(nameInDb = "default_assignee")
    Boolean defaultAssignee = false;

    @Property(nameInDb = "default_cc")
    Boolean defaultCC = false;


    @Generated(hash = 1444502823)
    public PunchlistAssignee(Integer usersId, Integer userId, String name, Boolean active,
            Integer pjProjectsId, Boolean defaultAssignee, Boolean defaultCC) {
        this.usersId = usersId;
        this.userId = userId;
        this.name = name;
        this.active = active;
        this.pjProjectsId = pjProjectsId;
        this.defaultAssignee = defaultAssignee;
        this.defaultCC = defaultCC;
    }
    @Generated(hash = 1720415236)
    public PunchlistAssignee() {
    }
    protected PunchlistAssignee(Parcel in) {
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        name = in.readString();
        byte tmpActive = in.readByte();
        active = tmpActive == 0 ? null : tmpActive == 1;
        if (in.readByte() == 0) {
            pjProjectsId = null;
        } else {
            pjProjectsId = in.readInt();
        }
    }

    public static final Creator<PunchlistAssignee> CREATOR = new Creator<PunchlistAssignee>() {
        @Override
        public PunchlistAssignee createFromParcel(Parcel in) {
            return new PunchlistAssignee(in);
        }

        @Override
        public PunchlistAssignee[] newArray(int size) {
            return new PunchlistAssignee[size];
        }
    };

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (usersId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(usersId);
        }
        if (userId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(userId);
        }
        parcel.writeString(name);
        parcel.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        if (pjProjectsId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(pjProjectsId);
        }
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
        return null;
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

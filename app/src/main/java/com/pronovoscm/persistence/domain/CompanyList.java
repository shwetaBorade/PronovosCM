package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "CompanyList")
public class CompanyList implements Parcelable {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "type")
    String type;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "selected")
    Boolean selected;
    @Property(nameInDb = "company_id")
    Integer companyId;
    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "is_deleted")
    Integer isDeleted;
    @Generated(hash = 1436423976)
    public CompanyList(Integer usersId, String type, String name, Boolean selected,
            Integer companyId, Integer pjProjectsId, Integer isDeleted) {
        this.usersId = usersId;
        this.type = type;
        this.name = name;
        this.selected = selected;
        this.companyId = companyId;
        this.pjProjectsId = pjProjectsId;
        this.isDeleted = isDeleted;
    }
    @Generated(hash = 1865630276)
    public CompanyList() {
    }

    protected CompanyList(Parcel in) {
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        type = in.readString();
        name = in.readString();
        byte tmpSelected = in.readByte();
        selected = tmpSelected == 0 ? null : tmpSelected == 1;
        if (in.readByte() == 0) {
            companyId = null;
        } else {
            companyId = in.readInt();
        }
        if (in.readByte() == 0) {
            pjProjectsId = null;
        } else {
            pjProjectsId = in.readInt();
        }
        if (in.readByte() == 0) {
            isDeleted = null;
        } else {
            isDeleted = in.readInt();
        }
    }

    public static final Creator<CompanyList> CREATOR = new Creator<CompanyList>() {
        @Override
        public CompanyList createFromParcel(Parcel in) {
            return new CompanyList(in);
        }

        @Override
        public CompanyList[] newArray(int size) {
            return new CompanyList[size];
        }
    };

    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getSelected() {
        return this.selected;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    public Integer getCompanyId() {
        return this.companyId;
    }
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }
    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }
    public Integer getIsDeleted() {
        return this.isDeleted;
    }
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeByte((byte) (selected == null ? 0 : selected ? 1 : 2));
        if (companyId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(companyId);
        }
        if (pjProjectsId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(pjProjectsId);
        }
        if (isDeleted == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(isDeleted);
        }
    }
}

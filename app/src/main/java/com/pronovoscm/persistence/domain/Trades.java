package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Trades")
public class Trades implements Parcelable {
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "trades_id")
    Integer tradesId;
    @Property(nameInDb = "tenant_id")
    Integer tenantId;
    @Generated(hash = 1531018809)
    public Trades(Date createdAt, Date updatedAt, Date deletedAt, Integer usersId,
            String name, Integer tradesId, Integer tenantId) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.usersId = usersId;
        this.name = name;
        this.tradesId = tradesId;
        this.tenantId = tenantId;
    }
    @Generated(hash = 1336720842)
    public Trades() {
    }

    protected Trades(Parcel in) {
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            tradesId = null;
        } else {
            tradesId = in.readInt();
        }
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readInt();
        }
    }

    public static final Creator<Trades> CREATOR = new Creator<Trades>() {
        @Override
        public Trades createFromParcel(Parcel in) {
            return new Trades(in);
        }

        @Override
        public Trades[] newArray(int size) {
            return new Trades[size];
        }
    };

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getTradesId() {
        return this.tradesId;
    }
    public void setTradesId(Integer tradesId) {
        this.tradesId = tradesId;
    }
    public Integer getTenantId() {
        return this.tenantId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
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
        parcel.writeString(name);
        if (tradesId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(tradesId);
        }
        if (tenantId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(tenantId);
        }
    }
}

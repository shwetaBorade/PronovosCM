package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Objects;

import androidx.annotation.Nullable;

@Entity(nameInDb = "Tags")
public class ImageTag implements Parcelable {

    @Index(unique = true)
    @Property(nameInDb = "id")
    Integer id;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "tenant_id")
    Integer tenantId;
    @Generated(hash = 1088476242)
    public ImageTag(Integer id, String name, Integer tenantId) {
        this.id = id;
        this.name = name;
        this.tenantId = tenantId;
    }
    @Generated(hash = 1767431860)
    public ImageTag() {
    }

    public ImageTag(ImageTag imageTag) {
        this.id = imageTag.id;
        this.tenantId = imageTag.tenantId;
        this.name = imageTag.name;
    }
    protected ImageTag(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readInt();
        }
    }

    public static final Creator<ImageTag> CREATOR = new Creator<ImageTag>() {
        @Override
        public ImageTag createFromParcel(Parcel in) {
            return new ImageTag(in);
        }

        @Override
        public ImageTag[] newArray(int size) {
            return new ImageTag[size];
        }
    };

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(tenantId);

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return id==((ImageTag)obj).getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}

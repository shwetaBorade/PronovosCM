package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "Pj_photos_folders_mobile")
public class PhotoFolder implements Parcelable {

    @Property(nameInDb = "pj_projects_id")
    Integer pjProjectsId;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "deleted_at")
    Date deletedAt;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "pj_photos_folder_id")
    Integer pjPhotosFolderId;
    @Property(nameInDb = "is_static")
    Integer isStatic;
    @Index(unique = true)
    @Id(autoincrement = true)
    @Property(nameInDb = "pj_photos_folder_mobile_id")
    Long pjPhotosFolderMobileId;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Generated(hash = 271836258)
    public PhotoFolder(Integer pjProjectsId, Integer usersId, Date createdAt,
            Date deletedAt, Boolean isSync, String name, Integer pjPhotosFolderId,
            Integer isStatic, Long pjPhotosFolderMobileId, Date updatedAt) {
        this.pjProjectsId = pjProjectsId;
        this.usersId = usersId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.isSync = isSync;
        this.name = name;
        this.pjPhotosFolderId = pjPhotosFolderId;
        this.isStatic = isStatic;
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
        this.updatedAt = updatedAt;
    }
    @Generated(hash = 1039815377)
    public PhotoFolder() {
    }
    public Integer getPjProjectsId() {
        return this.pjProjectsId;
    }
    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPjPhotosFolderId() {
        return this.pjPhotosFolderId;
    }
    public void setPjPhotosFolderId(Integer pjPhotosFolderId) {
        this.pjPhotosFolderId = pjPhotosFolderId;
    }
    public Integer getIsStatic() {
        return this.isStatic;
    }
    public void setIsStatic(Integer isStatic) {
        this.isStatic = isStatic;
    }
    public Long getPjPhotosFolderMobileId() {
        return this.pjPhotosFolderMobileId;
    }
    public void setPjPhotosFolderMobileId(Long pjPhotosFolderMobileId) {
        this.pjPhotosFolderMobileId = pjPhotosFolderMobileId;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<PhotoFolder> CREATOR = new Creator<PhotoFolder>() {
        @Override
        public PhotoFolder createFromParcel(Parcel in) {
            return new PhotoFolder(in);
        }

        @Override
        public PhotoFolder[] newArray(int size) {
            return new PhotoFolder[size];
        }
    };
    protected PhotoFolder(Parcel in) {
        if (in.readByte() == 0) {
            pjProjectsId = null;
        } else {
            pjProjectsId = in.readInt();
        }
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        isSync = in.readByte() != 0;
        name = in.readString();
        if (in.readByte() == 0) {
            pjPhotosFolderId = null;
        } else {
            pjPhotosFolderId = in.readInt();
        }
        if (in.readByte() == 0) {
            pjPhotosFolderMobileId = null;
        } else {
            pjPhotosFolderMobileId = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (pjProjectsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pjProjectsId);
        }
        if (usersId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(usersId);
        }
        dest.writeByte((byte) (isSync ? 1 : 0));
        dest.writeString(name);
        if (pjPhotosFolderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pjPhotosFolderId);
        }
        if (pjPhotosFolderMobileId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(pjPhotosFolderMobileId);
        }

    }
}

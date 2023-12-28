package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "DrawingList")
public class DrawingList implements Parcelable {
    @Property(nameInDb = "created_at")
    Date createdAt;
    @Property(nameInDb = "descriptions")
    String descriptions;
    @Property(nameInDb = "drawing_date")
    Date drawingDate;
    @Property(nameInDb = "drawing_name")
    String drawingName;
    @Property(nameInDb = "drawing_status")
    Integer drawingStatus;
    @Property(nameInDb = "drw_discipline")
    String drawingDiscipline;
    @Property(nameInDb = "drw_discipline_id")
    String drawingDisciplineId;
    @Property(nameInDb = "drw_drawings_id")
    Integer drawingsId;
    @Property(nameInDb = "drw_folders_id")
    Integer drwFoldersId;
    @Property(nameInDb = "image_org")
    String imageOrg;
    @Property(nameInDb = "image_thumb")
    String imageThumb;
    @Property(nameInDb = "is_sync")
    Boolean isSync;
    @Property(nameInDb = "pdf_org")
    String pdfOrg;
    @Property(nameInDb = "pdf_status")
    Integer pdfStatus;
    @Property(nameInDb = "revisited_num")
    Integer revisitedNum;
    @Property(nameInDb = "current_revision")
    Integer currentRevision;
    @Property(nameInDb = "status")
    Integer status;
    @Property(nameInDb = "updated_at")
    Date updatedAt;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "original_drw_id")
    Integer originalDrwId;
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    @Generated(hash = 660990528)
    public DrawingList(Date createdAt, String descriptions, Date drawingDate,
            String drawingName, Integer drawingStatus, String drawingDiscipline,
            String drawingDisciplineId, Integer drawingsId, Integer drwFoldersId,
            String imageOrg, String imageThumb, Boolean isSync, String pdfOrg,
            Integer pdfStatus, Integer revisitedNum, Integer currentRevision,
            Integer status, Date updatedAt, Integer usersId, Integer originalDrwId,
            Long id) {
        this.createdAt = createdAt;
        this.descriptions = descriptions;
        this.drawingDate = drawingDate;
        this.drawingName = drawingName;
        this.drawingStatus = drawingStatus;
        this.drawingDiscipline = drawingDiscipline;
        this.drawingDisciplineId = drawingDisciplineId;
        this.drawingsId = drawingsId;
        this.drwFoldersId = drwFoldersId;
        this.imageOrg = imageOrg;
        this.imageThumb = imageThumb;
        this.isSync = isSync;
        this.pdfOrg = pdfOrg;
        this.pdfStatus = pdfStatus;
        this.revisitedNum = revisitedNum;
        this.currentRevision = currentRevision;
        this.status = status;
        this.updatedAt = updatedAt;
        this.usersId = usersId;
        this.originalDrwId = originalDrwId;
        this.id = id;
    }
    @Generated(hash = 1342943641)
    public DrawingList() {
    }
    public DrawingList(int drawingsId, String drawingDiscipline) {
        this.drawingsId = drawingsId;
        this.drawingDiscipline = drawingDiscipline;
    }

    protected DrawingList(Parcel in) {
        descriptions = in.readString();
        drawingName = in.readString();
        if (in.readByte() == 0) {
            drawingStatus = null;
        } else {
            drawingStatus = in.readInt();
        }
        drawingDiscipline = in.readString();
        drawingDisciplineId = in.readString();
        if (in.readByte() == 0) {
            drawingsId = null;
        } else {
            drawingsId = in.readInt();
        }
        if (in.readByte() == 0) {
            drwFoldersId = null;
        } else {
            drwFoldersId = in.readInt();
        }
        imageOrg = in.readString();
        imageThumb = in.readString();
        byte tmpIsSync = in.readByte();
        isSync = tmpIsSync == 0 ? null : tmpIsSync == 1;
        pdfOrg = in.readString();
        if (in.readByte() == 0) {
            pdfStatus = null;
        } else {
            pdfStatus = in.readInt();
        }
        if (in.readByte() == 0) {
            revisitedNum = null;
        } else {
            revisitedNum = in.readInt();
        }
        if (in.readByte() == 0) {
            currentRevision = null;
        } else {
            currentRevision = in.readInt();
        }
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        if (in.readByte() == 0) {
            originalDrwId = null;
        } else {
            originalDrwId = in.readInt();
        }
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
    }

    public static final Creator<DrawingList> CREATOR = new Creator<DrawingList>() {
        @Override
        public DrawingList createFromParcel(Parcel in) {
            return new DrawingList(in);
        }

        @Override
        public DrawingList[] newArray(int size) {
            return new DrawingList[size];
        }
    };

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getDescriptions() {
        return this.descriptions;
    }
    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
    public Date getDrawingDate() {
        return this.drawingDate;
    }
    public void setDrawingDate(Date drawingDate) {
        this.drawingDate = drawingDate;
    }
    public String getDrawingName() {
        return this.drawingName;
    }
    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }
    public Integer getDrawingStatus() {
        return this.drawingStatus;
    }
    public void setDrawingStatus(Integer drawingStatus) {
        this.drawingStatus = drawingStatus;
    }
    public String getDrawingDiscipline() {
        return this.drawingDiscipline;
    }
    public void setDrawingDiscipline(String drawingDiscipline) {
        this.drawingDiscipline = drawingDiscipline;
    }
    public String getDrawingDisciplineId() {
        return this.drawingDisciplineId;
    }
    public void setDrawingDisciplineId(String drawingDisciplineId) {
        this.drawingDisciplineId = drawingDisciplineId;
    }
    public Integer getDrawingsId() {
        return this.drawingsId;
    }
    public void setDrawingsId(Integer drawingsId) {
        this.drawingsId = drawingsId;
    }
    public Integer getDrwFoldersId() {
        return this.drwFoldersId;
    }
    public void setDrwFoldersId(Integer drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }
    public String getImageOrg() {
        return this.imageOrg;
    }
    public void setImageOrg(String imageOrg) {
        this.imageOrg = imageOrg;
    }
    public String getImageThumb() {
        return this.imageThumb;
    }
    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }
    public Boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }
    public String getPdfOrg() {
        return this.pdfOrg;
    }
    public void setPdfOrg(String pdfOrg) {
        this.pdfOrg = pdfOrg;
    }
    public Integer getPdfStatus() {
        return this.pdfStatus;
    }
    public void setPdfStatus(Integer pdfStatus) {
        this.pdfStatus = pdfStatus;
    }
    public Integer getRevisitedNum() {
        return this.revisitedNum;
    }
    public void setRevisitedNum(Integer revisitedNum) {
        this.revisitedNum = revisitedNum;
    }
    public Integer getCurrentRevision() {
        return this.currentRevision;
    }
    public void setCurrentRevision(Integer currentRevision) {
        this.currentRevision = currentRevision;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getOriginalDrwId() {
        return this.originalDrwId;
    }
    public void setOriginalDrwId(Integer originalDrwId) {
        this.originalDrwId = originalDrwId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(descriptions);
        parcel.writeString(drawingName);
        if (drawingStatus == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(drawingStatus);
        }
        parcel.writeString(drawingDiscipline);
        parcel.writeString(drawingDisciplineId);
        if (drawingsId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(drawingsId);
        }
        if (drwFoldersId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(drwFoldersId);
        }
        parcel.writeString(imageOrg);
        parcel.writeString(imageThumb);
        parcel.writeByte((byte) (isSync == null ? 0 : isSync ? 1 : 2));
        parcel.writeString(pdfOrg);
        if (pdfStatus == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(pdfStatus);
        }
        if (revisitedNum == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(revisitedNum);
        }
        if (currentRevision == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(currentRevision);
        }
        if (status == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(status);
        }
        if (usersId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(usersId);
        }
        if (originalDrwId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(originalDrwId);
        }
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
    }

    @Override
    public String toString() {
        return "DrawingList{" +
                "drawingName='" + drawingName + '\'' +
                ", imageOrg='" + imageOrg + '\'' +
                ", imageThumb='" + imageThumb + '\'' +
                ", pdfOrg='" + pdfOrg + '\'' +
                ", pdfStatus=" + pdfStatus +
                ", currentRevision=" + currentRevision +
                '}';
    }
}

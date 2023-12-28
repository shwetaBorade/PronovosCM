package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

/**
 * Created on 5/11/18.
 *
 * @author GWL
 */
@Entity(nameInDb = "punchlist_drawing")
public class PunchlistDrawing {


    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "punch_list_id")
    private Integer punchlistId;

    @Property(nameInDb = "punch_list_id_mobile")
    private Long punchlistIdMobile;
    @Property(nameInDb = "drawing_name")
    String drawingName;
    @Property(nameInDb = "drw_discipline_id")
    String drwDisciplineId;
    @Property(nameInDb = "drw_folders_id")
    Integer drwFoldersId;
    @Property(nameInDb = "revisited_num")
    Integer revisitedNum;
    @Property(nameInDb = "drw_uploads_id")
    Integer drwUploadsId;
    @Property(nameInDb = "drw_disciplines_id")
    Integer drwDisciplinesId;
    @Property(nameInDb = "original_drw_id")
    Integer originalDrwId;
    @Property(nameInDb = "drw_drawings_id")
    Integer drawingsId;
    @Generated(hash = 1225313052)
    public PunchlistDrawing(Long id, Integer punchlistId, Long punchlistIdMobile,
            String drawingName, String drwDisciplineId, Integer drwFoldersId,
            Integer revisitedNum, Integer drwUploadsId, Integer drwDisciplinesId,
            Integer originalDrwId, Integer drawingsId) {
        this.id = id;
        this.punchlistId = punchlistId;
        this.punchlistIdMobile = punchlistIdMobile;
        this.drawingName = drawingName;
        this.drwDisciplineId = drwDisciplineId;
        this.drwFoldersId = drwFoldersId;
        this.revisitedNum = revisitedNum;
        this.drwUploadsId = drwUploadsId;
        this.drwDisciplinesId = drwDisciplinesId;
        this.originalDrwId = originalDrwId;
        this.drawingsId = drawingsId;
    }
    @Generated(hash = 981227694)
    public PunchlistDrawing() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getPunchlistId() {
        return this.punchlistId;
    }
    public void setPunchlistId(Integer punchlistId) {
        this.punchlistId = punchlistId;
    }
    public Long getPunchlistIdMobile() {
        return this.punchlistIdMobile;
    }
    public void setPunchlistIdMobile(Long punchlistIdMobile) {
        this.punchlistIdMobile = punchlistIdMobile;
    }
    public String getDrawingName() {
        return this.drawingName;
    }
    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }
    public String getDrwDisciplineId() {
        return this.drwDisciplineId;
    }
    public void setDrwDisciplineId(String drwDisciplineId) {
        this.drwDisciplineId = drwDisciplineId;
    }
    public Integer getDrwFoldersId() {
        return this.drwFoldersId;
    }
    public void setDrwFoldersId(Integer drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }
    public Integer getRevisitedNum() {
        return this.revisitedNum;
    }
    public void setRevisitedNum(Integer revisitedNum) {
        this.revisitedNum = revisitedNum;
    }
    public Integer getDrwUploadsId() {
        return this.drwUploadsId;
    }
    public void setDrwUploadsId(Integer drwUploadsId) {
        this.drwUploadsId = drwUploadsId;
    }
    public Integer getDrwDisciplinesId() {
        return this.drwDisciplinesId;
    }
    public void setDrwDisciplinesId(Integer drwDisciplinesId) {
        this.drwDisciplinesId = drwDisciplinesId;
    }
    public Integer getOriginalDrwId() {
        return this.originalDrwId;
    }
    public void setOriginalDrwId(Integer originalDrwId) {
        this.originalDrwId = originalDrwId;
    }
    public Integer getDrawingsId() {
        return this.drawingsId;
    }
    public void setDrawingsId(Integer drawingsId) {
        this.drawingsId = drawingsId;
    }
       
}

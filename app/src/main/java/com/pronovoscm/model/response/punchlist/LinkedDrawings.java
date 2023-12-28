package com.pronovoscm.model.response.punchlist;

import com.google.gson.annotations.SerializedName;

public class LinkedDrawings {
    @SerializedName("revisited_num")
    private int revisitedNum;
    @SerializedName("drawing_name")
    private String drawingName;
    @SerializedName("original_drw_id")
    private int originalDrwId;
    @SerializedName("drw_disciplines_id")
    private int drwDisciplinesId;
    @SerializedName("drw_discipline_id")
    private String drwDisciplineId;
    @SerializedName("drw_uploads_id")
    private int drwUploadsId;
    @SerializedName("drw_folders_id")
    private int drwFoldersId;
    @SerializedName("drw_drawings_id")
    private int drwDrawingsId;

    public int getRevisitedNum() {
        return revisitedNum;
    }

    public void setRevisitedNum(int revisitedNum) {
        this.revisitedNum = revisitedNum;
    }

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public int getOriginalDrwId() {
        return originalDrwId;
    }

    public void setOriginalDrwId(int originalDrwId) {
        this.originalDrwId = originalDrwId;
    }

    public int getDrwDisciplinesId() {
        return drwDisciplinesId;
    }

    public void setDrwDisciplinesId(int drwDisciplinesId) {
        this.drwDisciplinesId = drwDisciplinesId;
    }

    public String getDrwDisciplineId() {
        return drwDisciplineId;
    }

    public void setDrwDisciplineId(String drwDisciplineId) {
        this.drwDisciplineId = drwDisciplineId;
    }

    public int getDrwUploadsId() {
        return drwUploadsId;
    }

    public void setDrwUploadsId(int drwUploadsId) {
        this.drwUploadsId = drwUploadsId;
    }

    public int getDrwFoldersId() {
        return drwFoldersId;
    }

    public void setDrwFoldersId(int drwFoldersId) {
        this.drwFoldersId = drwFoldersId;
    }

    public int getDrwDrawingsId() {
        return drwDrawingsId;
    }

    public void setDrwDrawingsId(int drwDrawingsId) {
        this.drwDrawingsId = drwDrawingsId;
    }
}

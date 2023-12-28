package com.pronovoscm.model.response.drawinglist;

import com.google.gson.annotations.SerializedName;

public class Drwings {
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("drw_discipline")
    private String drw_discipline;
    @SerializedName("abbreviation")
    private String abbreviation;
    @SerializedName("original_drw_id")
    private int original_drw_id;
    @SerializedName("drw_discipline_id")
    private int drw_discipline_id;
    @SerializedName("status")
    private int status;
    @SerializedName("drawing_status")
    private int drawing_status;
    @SerializedName("xod_org")
    private String xod_org;
    @SerializedName("pdf_org")
    private String pdf_org;
    @SerializedName("image_org")
    private String image_org;
    @SerializedName("image_thumb")
    private String image_thumb;
    @SerializedName("drawing_date")
    private String drawing_date;
    @SerializedName("revisited_num")
    private int revisited_num;
    @SerializedName("description")
    private String description;
    @SerializedName("drawing_name")
    private String drawing_name;
    @SerializedName("drw_drawings_id")
    private int drw_drawings_id;
    @SerializedName("current_revision")
    private int currentRevision;

    @SerializedName("deleted_at")
    private String deletedAt;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDrw_discipline() {
        return drw_discipline;
    }

    public void setDrw_discipline(String drw_discipline) {
        this.drw_discipline = drw_discipline;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getDrw_discipline_id() {
        return drw_discipline_id;
    }

    public void setDrw_discipline_id(int drw_discipline_id) {
        this.drw_discipline_id = drw_discipline_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDrawing_status() {
        return drawing_status;
    }

    public void setDrawing_status(int drawing_status) {
        this.drawing_status = drawing_status;
    }

    public String getXod_org() {
        return xod_org;
    }

    public void setXod_org(String xod_org) {
        this.xod_org = xod_org;
    }

    public String getPdf_org() {
        return pdf_org;
    }

    public void setPdf_org(String pdf_org) {
        this.pdf_org = pdf_org;
    }

    public String getImage_org() {
        return image_org;
    }

    public void setImage_org(String image_org) {
        this.image_org = image_org;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDrawing_date() {
        return drawing_date;
    }

    public void setDrawing_date(String drawing_date) {
        this.drawing_date = drawing_date;
    }

    public int getRevisited_num() {
        return revisited_num;
    }

    public void setRevisited_num(int revisited_num) {
        this.revisited_num = revisited_num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDrawing_name() {
        return drawing_name;
    }

    public void setDrawing_name(String drawing_name) {
        this.drawing_name = drawing_name;
    }

    public int getDrw_drawings_id() {
        return drw_drawings_id;
    }

    public void setDrw_drawings_id(int drw_drawings_id) {
        this.drw_drawings_id = drw_drawings_id;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getOriginal_drw_id() {
        return original_drw_id;
    }

    public void setOriginal_drw_id(int original_drw_id) {
        this.original_drw_id = original_drw_id;
    }

    public int getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(int currentRevision) {
        this.currentRevision = currentRevision;
    }
}

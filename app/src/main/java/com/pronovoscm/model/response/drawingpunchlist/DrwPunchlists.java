package com.pronovoscm.model.response.drawingpunchlist;

import com.google.gson.annotations.SerializedName;

public class DrwPunchlists {
    @SerializedName("drw_punchlists_id")
    private int drwPunchlistsId;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("punch_lists_id")
    private int punchListsId;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("drw_drawings_id")
    private int drwDrawingsId;

    public int getDrwPunchlistsId() {
        return drwPunchlistsId;
    }

    public void setDrwPunchlistsId(int drwPunchlistsId) {
        this.drwPunchlistsId = drwPunchlistsId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getPunchListsId() {
        return punchListsId;
    }

    public void setPunchListsId(int punchListsId) {
        this.punchListsId = punchListsId;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getDrwDrawingsId() {
        return drwDrawingsId;
    }

    public void setDrwDrawingsId(int drwDrawingsId) {
        this.drwDrawingsId = drwDrawingsId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

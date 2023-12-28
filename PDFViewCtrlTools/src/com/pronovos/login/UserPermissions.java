package com.pronovos.login;

import com.google.gson.annotations.SerializedName;

public class UserPermissions {
    @SerializedName("view-form")
    private Integer viewForm;
    @SerializedName("edit-form")
    private Integer editForm;

    @SerializedName("create-request")
    private int createRequest;
    @SerializedName("create-transfer")
    private int createTransfer;
    @SerializedName("edit-project-transfers")
    private int editProjectTransfers;
    @SerializedName("view-drawings")
    private int viewDrawings;
    @SerializedName("drawing-toolbar")
    private int drawingToolbar;
    @SerializedName("create-drawings")
    private int createDrawings;
    @SerializedName("edit-drawings")
    private int editDrawings;
    @SerializedName("delete-drawings")
    private int deleteDrawings;
    @SerializedName("upload-photo")
    private int uploadPhoto;
    @SerializedName("edit-photo")
    private int editPhoto;
    @SerializedName("delete-photo")
    private int deletePhoto;
    @SerializedName("view-albums")
    private int viewAlbums;
    @SerializedName("create-album")
    private int createAlbum;
    @SerializedName("edit-albums")
    private int editAlbum;
    @SerializedName("delete-album")
    private int deleteAlbum;
    @SerializedName("create-project-daily-report")
    private int createProjectDailyReport;
    @SerializedName("edit-project-daily-report")
    private int editProjectDailyReport;
    @SerializedName("delete-project-daily-report")
    private int deleteProjectDailyReport;
    @SerializedName("create-punch-list")
    private int createPunchList;
    @SerializedName("delete-punch-list")
    private int deletePunchList;
    @SerializedName("edit-punch-list")
    private int editPunchList;
    @SerializedName("view-punch-list")
    private int viewPunchList;
    @SerializedName("view-project-daily-report")
    private int viewProjectDailyReport;
    @SerializedName("view-field-docs")
    private int viewFieldDocs;
    @SerializedName("view-project-inventory")
    private int viewProjectInventory;
    @SerializedName("view-project-equipment")
    private int viewProjectEquipment;
    @SerializedName("view-project-transfers")
    private int viewProjectTransfers;
    @SerializedName("view-document")
    private int viewDocument;
    @SerializedName("edit-document")
    private int editDocument;


    @SerializedName("view-rfi")
    private int viewRfi;
    @SerializedName("edit-rfi")
    private int editRfi;

    public int getViewRfi() {
        return viewRfi;
    }

    public void setViewRfi(int viewRfi) {
        this.viewRfi = viewRfi;
    }

    public int getEditRfi() {
        return editRfi;
    }

    public void setEditRfi(int editRfi) {
        this.editRfi = editRfi;
    }

    public int getViewDrawings() {
        return viewDrawings;
    }

    public void setViewDrawings(int viewDrawings) {
        this.viewDrawings = viewDrawings;
    }

    public int getDrawingToolbar() {
        return drawingToolbar;
    }

    public void setDrawingToolbar(int drawingToolbar) {
        this.drawingToolbar = drawingToolbar;
    }

    public int getCreateDrawings() {
        return createDrawings;
    }

    public void setCreateDrawings(int createDrawings) {
        this.createDrawings = createDrawings;
    }

    public int getEditDrawings() {
        return editDrawings;
    }

    public void setEditDrawings(int editDrawings) {
        this.editDrawings = editDrawings;
    }

    public int getDeleteDrawings() {
        return deleteDrawings;
    }

    public void setDeleteDrawings(int deleteDrawings) {
        this.deleteDrawings = deleteDrawings;
    }

    public int getUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(int uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public int getEditPhoto() {
        return editPhoto;
    }

    public void setEditPhoto(int editPhoto) {
        this.editPhoto = editPhoto;
    }

    public int getDeletePhoto() {
        return deletePhoto;
    }

    public void setDeletePhoto(int deletePhoto) {
        this.deletePhoto = deletePhoto;
    }

    public int getViewAlbums() {
        return viewAlbums;
    }

    public void setViewAlbums(int viewAlbums) {
        this.viewAlbums = viewAlbums;
    }

    public int getCreateAlbum() {
        return createAlbum;
    }

    public void setCreateAlbum(int createAlbum) {
        this.createAlbum = createAlbum;
    }

    public int getEditAlbum() {
        return editAlbum;
    }

    public void setEditAlbum(int editAlbum) {
        this.editAlbum = editAlbum;
    }

    public int getDeleteAlbum() {
        return deleteAlbum;
    }

    public void setDeleteAlbum(int deleteAlbum) {
        this.deleteAlbum = deleteAlbum;
    }

    public int getCreateProjectDailyReport() {
        return createProjectDailyReport;
    }

    public void setCreateProjectDailyReport(int createProjectDailyReport) {
        this.createProjectDailyReport = createProjectDailyReport;
    }

    public int getEditProjectDailyReport() {
        return editProjectDailyReport;
    }

    public void setEditProjectDailyReport(int editProjectDailyReport) {
        this.editProjectDailyReport = editProjectDailyReport;
    }

    public int getDeleteProjectDailyReport() {
        return deleteProjectDailyReport;
    }

    public void setDeleteProjectDailyReport(int deleteProjectDailyReport) {
        this.deleteProjectDailyReport = deleteProjectDailyReport;
    }

    public int getCreatePunchList() {
        return createPunchList;
    }

    public void setCreatePunchList(int createPunchList) {
        this.createPunchList = createPunchList;
    }

    public int getDeletePunchList() {
        return deletePunchList;
    }

    public void setDeletePunchList(int deletePunchList) {
        this.deletePunchList = deletePunchList;
    }

    public int getEditPunchList() {
        return editPunchList;
    }

    public void setEditPunchList(int editPunchList) {
        this.editPunchList = editPunchList;
    }

    public int getViewPunchList() {
        return viewPunchList;
    }

    public void setViewPunchList(int viewPunchList) {
        this.viewPunchList = viewPunchList;
    }

    public int getViewProjectDailyReport() {
        return viewProjectDailyReport;
    }

    public void setViewProjectDailyReport(int viewProjectDailyReport) {
        this.viewProjectDailyReport = viewProjectDailyReport;
    }

    public int getViewFieldDocs() {
        return viewFieldDocs;
    }

    public void setViewFieldDocs(int viewFieldDocs) {
        this.viewFieldDocs = viewFieldDocs;
    }

    public int getViewProjectInventory() {
        return viewProjectInventory;
    }

    public void setViewProjectInventory(int viewProjectInventory) {
        this.viewProjectInventory = viewProjectInventory;
    }

    public int getViewProjectEquipment() {
        return viewProjectEquipment;
    }

    public void setViewProjectEquipment(int viewProjectEquipment) {
        this.viewProjectEquipment = viewProjectEquipment;
    }

    public int getViewProjectTransfers() {
        return viewProjectTransfers;
    }

    public void setViewProjectTransfers(int viewProjectTransfers) {
        this.viewProjectTransfers = viewProjectTransfers;
    }

    public int getCreateRequest() {
        return createRequest;
    }

    public void setCreateRequest(int createRequest) {
        this.createRequest = createRequest;
    }

    public int getCreateTransfer() {
        return createTransfer;
    }

    public void setCreateTransfer(int createTransfer) {
        this.createTransfer = createTransfer;
    }

    public int getEditProjectTransfers() {
        return editProjectTransfers;
    }

    public void setEditProjectTransfers(int editProjectTransfers) {
        this.editProjectTransfers = editProjectTransfers;
    }

    public int getViewForm() {
        return viewForm;
    }

    public void setViewForm(int viewForm) {
        this.viewForm = viewForm;
    }

    public int getEditForm() {
        return editForm;
    }

    public void setEditForm(int editForm) {
        this.editForm = editForm;
    }

    public int getViewDocument() {
        return viewDocument;
    }

    public void setViewDocument(int viewDocument) {
        this.viewDocument = viewDocument;
    }

    public int getEditDocument() {
        return editDocument;
    }

    public void setEditDocument(int editDocument) {
        this.editDocument = editDocument;
    }
}

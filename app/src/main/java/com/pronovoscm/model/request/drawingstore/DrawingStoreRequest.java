package com.pronovoscm.model.request.drawingstore;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DrawingStoreRequest {


    @SerializedName("drawing_id")
    private int drawing_id;
    @SerializedName("annot_xml")
    private String annot_xml;
    @SerializedName("deleted_punch_annotation")
    private ArrayList<DeletedPunchAnnotation> deletedPunchAnnotation;


    public int getDrawing_id() {
        return drawing_id;
    }

    public void setDrawing_id(int drawing_id) {
        this.drawing_id = drawing_id;
    }

    public String getAnnot_xml() {
        return annot_xml;
    }

    public void setAnnot_xml(String annot_xml) {
        this.annot_xml = annot_xml;
    }

    public ArrayList<DeletedPunchAnnotation> getDeletedPunchAnnotation() {
        return deletedPunchAnnotation;
    }

    public void setDeletedPunchAnnotation(ArrayList<DeletedPunchAnnotation> deletedPunchAnnotation) {
        this.deletedPunchAnnotation = deletedPunchAnnotation;
    }
}

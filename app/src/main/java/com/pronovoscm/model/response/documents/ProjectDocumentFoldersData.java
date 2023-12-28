package com.pronovoscm.model.response.documents;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectDocumentFoldersData {

    @SerializedName("documentfolders")
    @Expose
    private List<Documentfolder> documentfolders = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;
    @SerializedName("pj_projects_id")
    @Expose
    private Integer pjProjectsId;

    public List<Documentfolder> getDocumentfolders() {
        return documentfolders;
    }

    public void setDocumentfolders(List<Documentfolder> documentfolders) {
        this.documentfolders = documentfolders;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

   /* public Integer getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(Integer pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }*/
}

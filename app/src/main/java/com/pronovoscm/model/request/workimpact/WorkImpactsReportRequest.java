package com.pronovoscm.model.request.workimpact;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkImpactsReportRequest {
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("type")
    private String type;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("work_impact_report_id_mobile")
    private String workImpactReportIdMobile;
    @SerializedName("attachments")
    private List<Attachments> attachments;
    @SerializedName("work_impact_report_id")
    private String workImpactReportId;
    @SerializedName("work_imp_location")
    private String workImpLocation;
    @SerializedName("company_id")
    private String companyId;
    @SerializedName("work_summary")
    private String workSummary;

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWorkImpactReportIdMobile() {
        return workImpactReportIdMobile;
    }

    public void setWorkImpactReportIdMobile(String workImpactReportIdMobile) {
        this.workImpactReportIdMobile = workImpactReportIdMobile;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public String getWorkImpactReportId() {
        return workImpactReportId;
    }

    public void setWorkImpactReportId(String workImpactReportId) {
        this.workImpactReportId = workImpactReportId;
    }

    public String getWorkImpLocation() {
        return workImpLocation;
    }

    public void setWorkImpLocation(String workImpLocation) {
        this.workImpLocation = workImpLocation;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getWorkSummary() {
        return workSummary;
    }

    public void setWorkSummary(String workSummary) {
        this.workSummary = workSummary;
    }
}

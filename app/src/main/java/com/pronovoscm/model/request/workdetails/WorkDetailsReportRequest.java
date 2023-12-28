package com.pronovoscm.model.request.workdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkDetailsReportRequest {
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("work_details_report_id_mobile")
    private String workDetailsReportIdMobile;
    @SerializedName("type")
    private String type;
    @SerializedName("work_details_report_id")
    private String workDetailsReportId;
    @SerializedName("work_det_location")
    private String workDetLocation;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("attachments")
    private List<Attachments> attachments;
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

    public String getWorkDetailsReportIdMobile() {
        return workDetailsReportIdMobile;
    }

    public void setWorkDetailsReportIdMobile(String workDetailsReportIdMobile) {
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWorkDetailsReportId() {
        return workDetailsReportId;
    }

    public void setWorkDetailsReportId(String workDetailsReportId) {
        this.workDetailsReportId = workDetailsReportId;
    }

    public String getWorkDetLocation() {
        return workDetLocation;
    }

    public void setWorkDetLocation(String workDetLocation) {
        this.workDetLocation = workDetLocation;
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

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
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

package com.pronovoscm.model.response.workimpact;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkImpactsReport {
    @SerializedName("attachments")
    private List<Attachments> attachments;
    @SerializedName("work_summary")
    private String workSummary;
    @SerializedName("work_imp_location")
    private String workImpLocation;
    @SerializedName("selected_company_id")
    private int selectedCompanyId;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("company_id")
    private int companyId;
    @SerializedName("type")
    private String type;
    @SerializedName("work_impact_report_id_mobile")
    private int workImpactReportIdMobile;
    @SerializedName("work_impact_report_id")
    private int workImpactReportId;
    @SerializedName("deleted_at")
    private String deletedAt;

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public String getWorkSummary() {
        return workSummary;
    }

    public void setWorkSummary(String workSummary) {
        this.workSummary = workSummary;
    }

    public String getWorkImpLocation() {
        return workImpLocation;
    }

    public void setWorkImpLocation(String workImpLocation) {
        this.workImpLocation = workImpLocation;
    }

    public int getSelectedCompanyId() {
        return selectedCompanyId;
    }

    public void setSelectedCompanyId(int selectedCompanyId) {
        this.selectedCompanyId = selectedCompanyId;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWorkImpactReportIdMobile() {
        return workImpactReportIdMobile;
    }

    public void setWorkImpactReportIdMobile(int workImpactReportIdMobile) {
        this.workImpactReportIdMobile = workImpactReportIdMobile;
    }

    public int getWorkImpactReportId() {
        return workImpactReportId;
    }

    public void setWorkImpactReportId(int workImpactReportId) {
        this.workImpactReportId = workImpactReportId;

    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

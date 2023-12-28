package com.pronovoscm.model.response.workdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkDetailsReport {
    @SerializedName("attachments")
    private List<Attachments> attachments;
    @SerializedName("work_summary")
    private String workSummary;
    @SerializedName("work_det_location")
    private String workDetLocation;
    @SerializedName("type")
    private String type;
    @SerializedName("selected_company_id")
    private int selectedCompanyId;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("company_id")
    private int companyId;
    @SerializedName("work_details_report_id_mobile")
    private int workDetailsReportIdMobile;
    @SerializedName("work_details_report_id")
    private int workDetailsReportId;

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

    public String getWorkDetLocation() {
        return workDetLocation;
    }

    public void setWorkDetLocation(String workDetLocation) {
        this.workDetLocation = workDetLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getWorkDetailsReportIdMobile() {
        return workDetailsReportIdMobile;
    }

    public void setWorkDetailsReportIdMobile(int workDetailsReportIdMobile) {
        this.workDetailsReportIdMobile = workDetailsReportIdMobile;
    }

    public int getWorkDetailsReportId() {
        return workDetailsReportId;
    }

    public void setWorkDetailsReportId(int workDetailsReportId) {
        this.workDetailsReportId = workDetailsReportId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}

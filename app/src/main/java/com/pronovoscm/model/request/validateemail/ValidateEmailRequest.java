package com.pronovoscm.model.request.validateemail;

import com.google.gson.annotations.SerializedName;

public class ValidateEmailRequest {
//{
//	"report_date": "2018-11-06 15:40:44",
//	"project_id": "221"
//}
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("report_date")
    private String reportDate;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

package com.pronovoscm.model.request.sendemail;

import com.google.gson.annotations.SerializedName;

public class SendEmailRequest {

    @SerializedName("users_id")
    private String usersId;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("data")
    private SendEmailData mSendEmailData;
    @SerializedName("report_date")
    private String reportDate;

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public SendEmailData getSendEmailData() {
        return mSendEmailData;
    }

    public void setSendEmailData(SendEmailData sendEmailData) {
        mSendEmailData = sendEmailData;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}

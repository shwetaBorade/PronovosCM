package com.pronovoscm.model.request.punchlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * "project_id":220,
 *     "punchlist_history": [{
 *         "punch_list_audits_id":0,
 *         "punch_list_audits_mobile_id": 12485998312,
 *         "punch_lists_id": 2910,
 *         "comments": "Test rejection",
 *         "status": 4,
 *         "created_by": 539,
 *         "created_at": "09/04/2022",
 *         "attachments":[{
 * 			"attachments_id": 0,
 * 			"attachments_id_mobile": 4711921247,
 * 			"attach_path": "https:\/\/s3.amazonaws.com\/dev.smartsubz.com\/punchlist_files\/16612549914M452747CY.jpg",
 * 			"deleted_at": ""
 *                }]
 *     }]
 */
public class PunchListHistory {
    @SerializedName("punch_list_audits_id")
    private int punchListAuditsId;
    @SerializedName("punch_list_audits_mobile_id")
    private String punchListAuditsMobileId;
    @SerializedName("punch_lists_id")
    private String punchListsId;
    @SerializedName("comments")
    private String comments = "";
    @SerializedName("status")
    private int status;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("attachments")
    private List<Attachments> attachments;

    @SerializedName("created_timestamp")
    private Long createdTimestamp;

    public int getPunchListAuditsId() {
        return punchListAuditsId;
    }

    public void setPunchListAuditsId(int punchListAuditsId) {
        this.punchListAuditsId = punchListAuditsId;
    }

    public String getPunchListAuditsMobileId() {
        return punchListAuditsMobileId;
    }

    public void setPunchListAuditsMobileId(String punchListAuditsMobileId) {
        this.punchListAuditsMobileId = punchListAuditsMobileId;
    }

    public String getPunchListsId() {
        return punchListsId;
    }

    public void setPunchListsId(String punchListsId) {
        this.punchListsId = punchListsId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}

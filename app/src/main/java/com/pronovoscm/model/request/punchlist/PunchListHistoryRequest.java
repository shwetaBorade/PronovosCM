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
public class PunchListHistoryRequest {
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("punchlist_history")
    private List<PunchListHistory> punchListHistories;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public List<PunchListHistory> getPunchListHistories() {
        return punchListHistories;
    }

    public void setPunchListHistories(List<PunchListHistory> punchListHistories) {
        this.punchListHistories = punchListHistories;
    }
}

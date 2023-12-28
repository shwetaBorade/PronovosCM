package com.pronovoscm.model.request.issueTracking

import com.google.gson.annotations.SerializedName

data class NewIssueRequest(
        @SerializedName("project_id")
        val projectId: Long = 0,
        @SerializedName("issue_trackings")
        val issuesList: List<IssueTracking> = emptyList()
)

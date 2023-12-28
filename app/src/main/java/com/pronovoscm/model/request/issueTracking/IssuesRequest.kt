package com.pronovoscm.model.request.issueTracking

import com.google.gson.annotations.SerializedName

data class IssuesRequest(
        @SerializedName("project_id")
        val projectId: Long = 0
)
package com.pronovoscm.model.request.issueTracking

import com.google.gson.annotations.SerializedName

data class IssuesImpactAndRootCause(
        @SerializedName("pj_issues_trackings_id")
        val pjIssuesTrackingsId: Long,
        @SerializedName("pj_issues_causeimpact_id")
        val pjIssuesCauseImpactId: Long,
        @SerializedName("type")
        val type: Int,
        @SerializedName("pj_issues_trackings_id_mobile")
        val pjIssuesTrackingIdMobile: Long,
        @SerializedName("deleted_at")
        val deletedAt: String
)

package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class IssuesImpactAndRootCause(
        @SerializedName("pj_issues_trackings_id")
        val pjIssuesTrackingsId: Long,
        @SerializedName("pj_issues_id")
        val pjIssuesId: Long,
        @SerializedName("pj_issues_causeimpact_id")
        val pjIssuesCauseImpactId: Long,
        @SerializedName("type")
        val type: Int,
        @SerializedName("pj_issues_trackings_id_mobile")
        val pjIssuesTrackingIdMobile: Long,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: String?
)

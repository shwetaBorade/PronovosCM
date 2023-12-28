package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class IssueItemBreakdown(
        @SerializedName("pj_issues_item_breakdowns_id")
        val pjIssuesItemBreakdownId: Long,
        @SerializedName("pj_issues_id")
        val pjIssuesId: Long,
        @SerializedName("description")
        val description: String,
        @SerializedName("days")
        val days: Int,
        @SerializedName("amount")
        val amount: Long,
        @SerializedName("pj_issues_item_breakdowns_id_mobile")
        val pjIssuesItemBreakdownIdMobile: Long,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: String?
)

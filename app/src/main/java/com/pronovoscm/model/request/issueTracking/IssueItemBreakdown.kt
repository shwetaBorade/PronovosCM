package com.pronovoscm.model.request.issueTracking

import com.google.gson.annotations.SerializedName

data class IssueItemBreakdown(
        @SerializedName("pj_issues_item_breakdowns_id")
        val pjIssuesItemBreakdownId: Long,
        @SerializedName("description")
        val description: String,
        @SerializedName("days")
        val days: Int,
        @SerializedName("amount")
        val amount: Long,
        @SerializedName("pj_issues_item_breakdowns_id_mobile")
        val pjIssuesItemBreakdownIdMobile: Long,
        @SerializedName("deleted_at")
        val deletedAt: String
)

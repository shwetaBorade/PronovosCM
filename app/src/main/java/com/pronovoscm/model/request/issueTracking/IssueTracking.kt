package com.pronovoscm.model.request.issueTracking

import com.google.gson.annotations.SerializedName
import com.pronovoscm.model.response.issueTracking.issues.Assignee
import com.pronovoscm.model.view.CustomFields
import com.pronovoscm.model.view.CustomFieldsValues
import com.pronovoscm.model.view.IssueListItemCustomFields

data class IssueTracking(
        @SerializedName("pj_issues_id")
        val pjIssueId: Long,
        @SerializedName("pj_projects_id")
        val pjProjectsId: Long,
        @SerializedName("title")
        val title: String,
        @SerializedName("date_created")
        val dateCreated: String,
        @SerializedName("is_resolved")
        val resolvedStatus: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("pj_issues_id_mobile")
        val pjIssueIdMobile: Long?,
        @SerializedName("deleted_at")
        val deletedAt: String,
        @SerializedName("date_resolved")
        val resolvedDate: String,
        @SerializedName("impacts")
        val impacts: List<IssuesImpactAndRootCause>?,
        @SerializedName("root_cause")
        val rootCause: List<IssuesImpactAndRootCause>?,
        @SerializedName("item_breakdowns")
        val itemsBreakdown: List<IssueItemBreakdown>?,
        @SerializedName("needed_by")
        val neededBy:String,
        @SerializedName("assignee")
        val assignee:Assignee,
        @SerializedName("custom_field")
        val customFields: List<IssueListItemCustomFields>,
        @SerializedName("needed_by_timezone")
        val neededByTimezone: String
)
package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName
import com.pronovoscm.model.view.IssueListItemCustomFields

data class IssueTracking(
    @SerializedName("pj_issues_id")
    val pjIssueId: Long,
    @SerializedName("pj_projects_id")
    val pjProjectsId: Long,
    @SerializedName("users_id")
    val usersId: Long,
    @SerializedName("tenant_id")
    val tenantId: Long,
    @SerializedName("issue_number")
    val issueNumber: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("date_resolved")
    val dateResolved: String?,
    @SerializedName("is_resolved")
    val resolvedStatus: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("pj_issues_id_mobile")
    val pjIssueIdMobile: Long?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: String?,
    @SerializedName("impacts")
    val impacts: List<IssuesImpactAndRootCause>?,
    @SerializedName("root_causes")
    val rootCause: List<IssuesImpactAndRootCause>?,
    @SerializedName("item_breakdowns")
    val itemsBreakdown: List<IssueItemBreakdown>?,
    @SerializedName("created_by")
    val issueCreatedBy: IssueCreatedBy,
    @SerializedName("needed_by")
    val neededBy: String?,
    @SerializedName("assignee")
    val assignee: Assignee?,
    @SerializedName("custom_field")
    val customField: List<IssueListItemCustomFields>?,
    @SerializedName("needed_by_timezone")
    val neededByTimezone: String
)
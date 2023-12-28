package com.pronovoscm.model.view

interface ImpactAndRootCause

data class IssueImpactAndRootCause(
        val pjIssuesTrackingsId: Long,
        val pjIssuesId: Long,
        val pjIssuesCauseImpactId: Long,
        val type: Int,
        val pjIssuesTrackingIdMobile: Long,
        val iconUrl: String,
        val name: String = "",
        var createdAt: String,
        var updatedAt: String? = null,
        var deletedAt: String? = null,
        var cacheId: Long? = null,
) : ImpactAndRootCause

data class MasterImpactAndRootCause(
        val pjIssueCauseImpactId: Long,
        val name: String,
        val impactStatus: Int,
        val iconUrl: String,
        var isSelected: Boolean = false
) : ImpactAndRootCause

data class IssueStatus(
        val id: Int,
        val name: String,
        var isResolved: Boolean?,
        var isSelected: Boolean = false
) : ImpactAndRootCause
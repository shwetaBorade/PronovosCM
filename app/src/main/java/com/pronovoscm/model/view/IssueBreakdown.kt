package com.pronovoscm.model.view

data class IssueBreakdown(
        var pjIssuesItemBreakdownId: Long = 0,
        var pjIssuesItemBreakdownIdMobile: Long = 0,
        var pjIssuesId: Long = 0,
        var pjIssuesIdMobile: Long = 0,
        var description: String? = null,
        var days: Int? = null,
        var amount: Long? = null,
        var isDeleted: Boolean = false,
        var createdAt: String? = null,
        var updatedAt: String? = null,
        var deletedAt: String? = null,
        var number: Int = 0,
        var cacheId: Long? = null
)
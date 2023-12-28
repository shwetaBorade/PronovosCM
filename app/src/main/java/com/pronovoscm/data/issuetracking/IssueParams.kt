package com.pronovoscm.data.issuetracking


data class IssueListRequestParam(
        val projectId: Long,
        val searchQuery: String?,
        val status: Boolean?,
        val rootCauseAndImpactIds: List<Long>,
        val source: DataSource = DataSource.CACHE
)

data class IssueListResponse<T>(
        val data: T,
        val issueDataProcess: IssueDataProcess,
        val source: DataSource
)

enum class DataSource {
    CACHE, NETWORK, FORCE_REFRESH_NETWORK
}

enum class IssueDataProcess {
    LIST, ADD, UPDATE, FORCE_REFRESH
}

package com.pronovoscm.data.issuetracking

import android.content.Context
import com.pronovoscm.api.ProjectsApi
import com.pronovoscm.data.ProviderResult
import com.pronovoscm.model.response.AbstractCallback
import com.pronovoscm.model.response.issueTracking.issues.IssuesResponse
import com.pronovoscm.model.response.issueTracking.issues.IssuesResponseListData
import com.pronovoscm.model.view.IssueListItem
import com.pronovoscm.persistence.repository.ProjectIssueTrackingRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ProjectIssueTrackingProviderTest {

    @Mock
    private lateinit var mContext: Context

    @Mock
    private lateinit var repository: ProjectIssueTrackingRepository

    @Mock
    private lateinit var projectsApi: ProjectsApi

    private lateinit var provider: ProjectIssueTrackingProvider

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this@ProjectIssueTrackingProviderTest)

        provider = ProjectIssueTrackingProvider(
                context = mContext,
                repository = repository,
                projectsApi = projectsApi
        )
    }

    @Test
    fun getIssueList() {

        val issueListRequestParam = IssueListRequestParam(
                projectId = 32525,
                searchQuery = null,
                status = null,
                rootCauseAndImpactIds = emptyList(),
                source = DataSource.NETWORK
        )

        val expected = IssuesResponse().apply {
            status = 200
            message = "Retrieved project issue tracking."
            data = IssuesResponseListData(
                    issuesList = emptyList(),
                    responseCode = 101,
                    responseMsg = "Retrieved project issue tracking."
            )
        }

        val call: Call<IssuesResponse> = mock()

        whenever { projectsApi.getIssues(any(), any()) }.then {
            call
        }

        Mockito.doAnswer {

            val abstractCallback: AbstractCallback<IssuesResponse> = it.getArgument(0)

            abstractCallback.onResponse(call, Response.success(expected))

            null
        }.`when`(call).enqueue(any<AbstractCallback<IssuesResponse>>())

        val callback: ProviderResult<IssueListResponse<List<IssueListItem>>> = mock()


        provider.getIssueList(
                issueListRequestParam = issueListRequestParam,
                issuesResponseResult = callback
        )

       // assertEquals(expected, )
    }

    @Test
    fun getImpactsAndRootCause() {
    }

    @Test
    fun syncIssuesToServer() {
    }
}
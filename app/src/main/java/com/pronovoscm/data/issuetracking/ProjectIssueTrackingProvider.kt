package com.pronovoscm.data.issuetracking

import android.content.Context
import com.google.gson.Gson
import com.pronovoscm.PronovosApplication
import com.pronovoscm.R
import com.pronovoscm.api.ProjectsApi
import com.pronovoscm.data.ProviderResult
import com.pronovoscm.data.issuetracking.DataSource.CACHE
import com.pronovoscm.data.issuetracking.DataSource.FORCE_REFRESH_NETWORK
import com.pronovoscm.data.issuetracking.DataSource.NETWORK
import com.pronovoscm.model.SyncDataEnum
import com.pronovoscm.model.request.issueTracking.IssuesRequest
import com.pronovoscm.model.request.issueTracking.NewIssueRequest
import com.pronovoscm.model.response.AbstractCallback
import com.pronovoscm.model.response.ErrorResponse
import com.pronovoscm.model.response.issueTracking.impactAndRootCause.ImpactAndRootCauseResponse
import com.pronovoscm.model.response.issueTracking.issues.CustomItemTypesResponse
import com.pronovoscm.model.response.issueTracking.issues.CustomItemsResponse
import com.pronovoscm.model.response.issueTracking.issues.IssueSectionResponse
import com.pronovoscm.model.response.issueTracking.issues.IssuesResponse
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.model.view.CustomFields
import com.pronovoscm.model.view.IssueListItem
import com.pronovoscm.persistence.domain.DaoSession
import com.pronovoscm.persistence.domain.TransactionLogMobile
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemTypesCache
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemsCache
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingSectionCache
import com.pronovoscm.persistence.repository.ProjectIssueTrackingRepository
import com.pronovoscm.services.NetworkService
import com.pronovoscm.utils.SharedPref
import java.util.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Response

class ProjectIssueTrackingProvider(
    private val context: Context,
    private val projectsApi: ProjectsApi,
    private val repository: ProjectIssueTrackingRepository,
) {
    private val daoSession: DaoSession? = null

    /**
     * To call api for the list of issues
     *
     * @return all list of issues
     */
    @Synchronized
    fun getIssueList(
        issueListRequestParam: IssueListRequestParam,
        issuesResponseResult: ProviderResult<IssueListResponse<List<IssueListItem>>>
    ): Boolean = when (issueListRequestParam.source) {
        CACHE -> {
            getIssuesCache(
                issueListRequestParam.searchQuery,
                issueListRequestParam.status,
                issueListRequestParam.rootCauseAndImpactIds,
                issueListRequestParam.projectId,
                issuesResponseResult
            )
        }

        NETWORK -> {
            getImpactsAndRootCause(
                issueListRequestParam.source,
                issueListRequestParam.projectId,
                issuesResponseResult
            )
            /*getIssuesNetwork(
                issueListRequestParam.source,
                issueListRequestParam.projectId,
                issuesResponseResult
            )*/
        }

        FORCE_REFRESH_NETWORK -> {
            getImpactsAndRootCause(
                issueListRequestParam.source,
                issueListRequestParam.projectId,
                issuesResponseResult
            )
            /*getIssuesNetwork(
                issueListRequestParam.source,
                issueListRequestParam.projectId,
                issuesResponseResult
            )*/
        }
    }

    private fun getIssuesNetwork(
        source: DataSource,
        projectId: Long,
        issuesResponseResult: ProviderResult<IssueListResponse<List<IssueListItem>>>
    ) = if (NetworkService.isNetworkAvailable(context)) {

        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )

        val issuesRequest = IssuesRequest(projectId)
        val headers = HashMap<String, String>()
        headers["timezone"] = TimeZone.getDefault().id
        headers["lastupdate"] = repository.getProjectIssuesLastUpdateDate(projectId = projectId)
        if (loginResponse?.userDetails != null) {
            headers["Authorization"] = "Bearer " + loginResponse?.userDetails?.authtoken
        } else {
            issuesResponseResult.AccessTokenFailure("")
        }
        val isEmptyCache = repository.getIssues(projectId)?.isEmpty() ?: true

//        if (getImpactsAndRootCause().isEmpty()) {
//            getImpactsAndRootCause(source, projectId, issuesResponseResult)
//        } else {

        val issuesResponseCall: Call<IssuesResponse> = projectsApi.getIssues(headers, issuesRequest)

        issuesResponseCall.enqueue(object : AbstractCallback<IssuesResponse?>() {
            override fun handleSuccess(response: Response<IssuesResponse?>?) {
                if (response?.body() != null) {
                    var issuesResponse: IssuesResponse? = null
                    try {
                        issuesResponse = response.body()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        issuesResponseResult.failure("response null")
                    }

                    if (issuesResponse?.status == 200 && (issuesResponse.data?.responseCode == 101 || issuesResponse.data?.responseCode == 102)) {

                        loginResponse?.userDetails?.let {
                            repository.addIssues(issuesResponse.data?.issuesList ?: emptyList(),
                                it.users_id)
                        }

                        issuesResponseResult.success(
                            IssueListResponse(
                                data = repository.getIssues(projectId) ?: emptyList(),
                                issueDataProcess = IssueDataProcess.LIST,
                                source = source
                            )
                        )

                    } else if (issuesResponse != null && source != FORCE_REFRESH_NETWORK) {
                        issuesResponseResult.failure(issuesResponse.message)
                    } else if (source != FORCE_REFRESH_NETWORK) {
                        issuesResponseResult.failure("response null")
                    }
                }
            }

            override fun handleFailure(call: Call<IssuesResponse?>?, throwable: Throwable?) {
                if (source != FORCE_REFRESH_NETWORK) {
                    return
                }
                issuesResponseResult.failure(throwable?.message)
            }

            override fun handleError(call: Call<IssuesResponse?>?, errorResponse: ErrorResponse?) {
                if (source != FORCE_REFRESH_NETWORK) {
                    return
                }
                if (errorResponse != null && errorResponse.data != null && errorResponse.data.responsecode == 103) {
                    issuesResponseResult.AccessTokenFailure(errorResponse.message)
                } else {
                    issuesResponseResult.failure(errorResponse?.message)
                }
            }
        })
//        }

        isEmptyCache || source == NETWORK
    } else {
        val cacheIssues = repository.getIssues(projectId) ?: emptyList()

        if (cacheIssues.isNotEmpty())
            issuesResponseResult.success(
                IssueListResponse(
                    data = cacheIssues,
                    issueDataProcess = IssueDataProcess.LIST,
                    source = source
                )
            )
        else
            issuesResponseResult.failure("Issues have not been synced for offline access.\nReconnect to internet to sync issues.")
        false
    }

    private fun getIssuesCache(
        searchQuery: String?,
        status: Boolean?,
        rootCauseAndImpactId: List<Long>,
        projectId: Long,
        issuesDataCallback: ProviderResult<IssueListResponse<List<IssueListItem>>>
    ) = repository.filterIssueList(searchQuery, status, rootCauseAndImpactId, projectId)
        .let { list ->
            if (searchQuery != null || status != null || rootCauseAndImpactId.isNotEmpty() || list.isNotEmpty()) {
                issuesDataCallback.success(
                    IssueListResponse(
                        data = list,
                        issueDataProcess = IssueDataProcess.LIST,
                        source = CACHE
                    )
                )
                false
            } else {
                getImpactsAndRootCause(
                    source = CACHE,
                    projectId = projectId,
                    callback = issuesDataCallback
                )
                /*getIssuesNetwork(
                        source = CACHE,
                        projectId = projectId,
                        issuesDataCallback
                )*/
            }
        }

    fun getIssueDetails(
        pjIssueId: Long,
        pjIssuesIdMobile: Long,
        issueListItemResult: ProviderResult<IssueListItem>
    ) = repository.getIssueDetails(pjIssueId, pjIssuesIdMobile)?.let { safeItem ->
        issueListItemResult.success(safeItem)
    } ?: run {
        issueListItemResult.failure(context.getString(R.string.some_error_occurred_while_fetching_details))
    }

    private fun getImpactsAndRootCause(
        source: DataSource,
        projectId: Long,
        callback: ProviderResult<IssueListResponse<List<IssueListItem>>>
    ): Boolean {
        if (NetworkService.isNetworkAvailable(context)) {

            val loginResponse: LoginResponse? = Gson().fromJson(
                SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse::class.java
            )

            val authorization: String = if (loginResponse?.userDetails != null) {
                "Bearer ${loginResponse?.userDetails?.authtoken}"
            } else {
                callback.AccessTokenFailure("")
                ""
            }

            val lastUpdate = repository.getImpactAndRootCauseLastUpdateDate()

            val impactAndRootCauseResponseCall = projectsApi.getImpactAndRootCauseResponse(
                hashMapOf(
                    "lastupdate" to lastUpdate,
                    "Authorization" to authorization
                )
            )
            val isEmptyCache = repository.getIssues(projectId)?.isEmpty() ?: true
            impactAndRootCauseResponseCall.enqueue(object :
                AbstractCallback<ImpactAndRootCauseResponse>() {

                override fun handleSuccess(response: Response<ImpactAndRootCauseResponse>?) {
                    if (response?.isSuccessful == true) {
                        val impactAndRootCauseResponse: ImpactAndRootCauseResponse? =
                            response.body()

                        impactAndRootCauseResponse?.let { safeResponse ->

                            if (safeResponse.status == 200 && (safeResponse.data?.responseCode == 101 || safeResponse.data?.responseCode == 102)) {

                                val impactAndRootCauseList =
                                    safeResponse.data?.impactAndRootCausesList
                                        ?: emptyList()

                                if (impactAndRootCauseList.isNotEmpty()) {
                                   repository.deleteImpactsAndRootCause()
                                    repository.addImpactsAndRootCause(impactAndRootCauseList)
                                }

                                getIssuesNetwork(
                                    source = source,
                                    projectId = projectId,
                                    issuesResponseResult = callback
                                )

                            } else {
                                callback.failure(safeResponse.message)
                            }

                        } ?: callback.failure("response false")

                    }
                }

                override fun handleFailure(
                    call: Call<ImpactAndRootCauseResponse>?,
                    throwable: Throwable?
                ) {
                    callback.failure(throwable?.message)
                }

                override fun handleError(
                    call: Call<ImpactAndRootCauseResponse>?,
                    errorResponse: ErrorResponse?
                ) {
                    if (errorResponse != null && errorResponse.data != null && errorResponse.data.responsecode == 103) {
                        callback.AccessTokenFailure(errorResponse.message)
                    } else {
                        callback.failure(errorResponse?.message)
                    }
                }
            })
            return isEmptyCache || source == NETWORK
        } else {
            callback.success(
                IssueListResponse(
                    data = emptyList(),
                    issueDataProcess = IssueDataProcess.LIST,
                    source = source
                )
            )
            return false
        }
    }

    fun getImpactsAndRootCause() = repository.getImpactAndRootCause()

    fun storeImpactAndRootCauseFilePath(
        iconStoragePath: String,
        pjIssueCauseImpactId: Long
    ) {
        repository.updateImpactAndRootCauseIconPath(
            iconStoragePath,
            pjIssueCauseImpactId
        )
    }

    fun storeIssue(issueListItem: IssueListItem, customFieldValues: ArrayList<CustomFields>) =
        repository.addIssue(issueListItem = issueListItem,customFieldValues = customFieldValues)

    fun updateIssue(issueListItem: IssueListItem, customFieldValues: ArrayList<CustomFields>) =
        repository.updateIssue(issueListItem = issueListItem, customFieldValues = customFieldValues)

    fun generateMobileId(type: MobileIdType): Long {
        val timeSeed = System.nanoTime() // to get the current date time value

        val randSeed = Math.random() * 1000 // random number generation

        var mobileId = (timeSeed * randSeed).toLong()

        val s = mobileId.toString() + ""
        val subStr = s.substring(0, 9)
        mobileId = subStr.toLong()

        repository.isValidMobileId(mobileId, type)

        if (repository.isValidMobileId(mobileId, type).not()) {
            generateMobileId(type)
        }

        return mobileId
    }

    fun getUserId(): Long {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        return loginResponse?.userDetails?.users_id?.toLong() ?: 0L
    }

    fun getCreatedByName(): String {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        return "${loginResponse?.userDetails?.firstname} ${loginResponse?.userDetails?.lastname}"
    }

    fun getTenantId(): Long {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        return loginResponse?.userDetails?.tenantId?.toLong() ?: 0L
    }

    fun deleteIssue(pjIssueId: Long, pjIssueIdMobile: Long) {
        repository.deleteIssue(pjIssueId, pjIssueIdMobile)
    }

    fun syncIssuesToServer(
        transactionLogMobile: TransactionLogMobile
    ) {
        if (NetworkService.isNetworkAvailable(context)) {

            val requestList = repository.getUnSyncedIssues()

            requestList?.let { safeIssuesRequest ->

                if (safeIssuesRequest.isNotEmpty()) {

                    val loginResponse: LoginResponse? = Gson().fromJson(
                        SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
                        LoginResponse::class.java
                    )

                    val headers = HashMap<String, String>()
                    headers["timezone"] = TimeZone.getDefault().id
                    headers["lastupdate"] =
                        repository.getProjectIssuesLastUpdateDate(projectId = null)
                    if (loginResponse?.userDetails != null) {
                        headers["Authorization"] = "Bearer " + loginResponse?.userDetails?.authtoken
                    }

                    val issuesRequest = NewIssueRequest(
                        projectId = safeIssuesRequest[0].pjProjectsId,
                        issuesList = safeIssuesRequest
                    )

                    val allIds: List<Long> = safeIssuesRequest.map { it.pjIssueIdMobile ?: 0L }

                    repository.apply {
                        updateIssueProcessStatus(
                            mobileIds = allIds,
                            inProcess = true
                        )

                        updateMobileLogSyncStatus(
                            mobileIds = allIds,
                            status = SyncDataEnum.PROCESSING
                        )
                    }

                    val issueTrackingApiCall: Call<IssuesResponse> =
                        projectsApi.postIssues(headers, issuesRequest)

                    issueTrackingApiCall.enqueue(object : AbstractCallback<IssuesResponse>() {

                        override fun handleSuccess(response: Response<IssuesResponse>?) {
                            if (response?.body() != null) {
                                var issuesResponse: IssuesResponse? = null
                                try {
                                    issuesResponse = response.body()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (issuesResponse?.status == 200 && (issuesResponse.data?.responseCode == 101 || issuesResponse.data?.responseCode == 102)) {

                                    repository.apply {
                                        updateIssueProcessStatus(
                                            mobileIds = allIds,
                                            inProcess = false
                                        )

                                        updateIssueSyncStatus(
                                            mobileIds = allIds,
                                            isSync = true
                                        )

                                        deleteMobileLogs(
                                            mobileIds = allIds,
                                        )
                                    }

                                    loginResponse?.userDetails?.let {
                                        repository.addIssues(
                                            issuesResponse.data?.issuesList
                                                ?: emptyList(), it.users_id
                                        )
                                    }

                                    EventBus.getDefault().post(
                                        IssueListResponse(
                                            data = emptyList<IssueListItem>(),
                                            issueDataProcess = IssueDataProcess.FORCE_REFRESH,
                                            source = FORCE_REFRESH_NETWORK
                                        )
                                    )

                                    /*
                                    * Update Mobile Log DB.
                                    * Update Local Records
                                    * */

                                } else if (issuesResponse != null) {
                                    repository.apply {
                                        updateIssueProcessStatus(
                                            mobileIds = allIds,
                                            inProcess = false
                                        )

                                        updateIssueSyncStatus(
                                            mobileIds = allIds,
                                            isSync = false
                                        )

                                        updateMobileLogSyncStatus(
                                            mobileIds = allIds,
                                            status = SyncDataEnum.SYNC_FAILED
                                        )
                                    }
                                }
                                PronovosApplication.getContext()?.setupAndStartWorkManager()
                            }
                        }

                        override fun handleFailure(
                            call: Call<IssuesResponse>?,
                            throwable: Throwable?
                        ) {
                            repository.apply {
                                updateIssueProcessStatus(
                                    mobileIds = allIds,
                                    inProcess = false
                                )

                                updateIssueSyncStatus(
                                    mobileIds = allIds,
                                    isSync = false
                                )

                                updateMobileLogSyncStatus(
                                    mobileIds = allIds,
                                    status = SyncDataEnum.SYNC_FAILED
                                )
                            }
                            PronovosApplication.getContext()?.setupAndStartWorkManager()
                        }

                        override fun handleError(
                            call: Call<IssuesResponse>?,
                            errorResponse: ErrorResponse?
                        ) {
                            repository.apply {
                                updateIssueProcessStatus(
                                    mobileIds = allIds,
                                    inProcess = false
                                )

                                updateIssueSyncStatus(
                                    mobileIds = allIds,
                                    isSync = false
                                )

                                updateMobileLogSyncStatus(
                                    mobileIds = allIds,
                                    status = SyncDataEnum.SYNC_FAILED
                                )
                            }
                            PronovosApplication.getContext()?.setupAndStartWorkManager()
                        }
                    })
                }
            }

        }
    }

    fun getIssueSections() {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val headers = HashMap<String, String>()
        headers["timezone"] = TimeZone.getDefault().id
        headers["lastupdate"] = repository.getSectionLastUpdatedDate()
        if (loginResponse?.userDetails != null) {
            headers["Authorization"] = "Bearer " + loginResponse.userDetails?.authtoken
        }
        val issueSections: Call<IssueSectionResponse> = projectsApi.getIssueSection(headers)
        issueSections.enqueue(object : AbstractCallback<IssueSectionResponse>() {
            override fun handleFailure(call: Call<IssueSectionResponse>?, throwable: Throwable?) {
                if (throwable != null) {
                }
            }

            override fun handleError(
                call: Call<IssueSectionResponse>?,
                errorResponse: ErrorResponse?
            ) {
            }

            override fun handleSuccess(response: Response<IssueSectionResponse>?) {
                if (response != null) {
                    response.body()?.data?.let {
                        loginResponse?.userDetails?.let { it1 ->
                            repository.addSection(
                                it.pjTrackingSections,
                                it1.users_id
                            )
                        }
                    }
                } else {
                }
            }

        })
    }

    fun getCustomItems() {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val headers = HashMap<String, String>()
        headers["timezone"] = TimeZone.getDefault().id
        headers["lastupdate"] = repository.getCustomItemsLastUpdatedDate()
        if (loginResponse?.userDetails != null) {
            headers["Authorization"] = "Bearer " + loginResponse.userDetails?.authtoken
        }
        val customTypes: Call<CustomItemsResponse> = projectsApi.getCustomItems(headers)
        customTypes.enqueue(object : AbstractCallback<CustomItemsResponse>() {
            override fun handleFailure(call: Call<CustomItemsResponse>?, throwable: Throwable?) {

            }

            override fun handleError(
                call: Call<CustomItemsResponse>?,
                errorResponse: ErrorResponse?
            ) {

            }

            override fun handleSuccess(response: Response<CustomItemsResponse>?) {
                if (response != null) {
                    response.body()?.data.let { item ->
                        if (item != null) {
                            loginResponse?.userDetails?.let {
                                repository.addCustomItems(
                                    item.pjTrackingItems,
                                    it.users_id
                                )
                            }
                        }
                    }
                } else {
                }
            }

        })
    }

    fun getCustomItemTypes() {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val headers = HashMap<String, String>()
        headers["timezone"] = TimeZone.getDefault().id
        headers["lastupdate"] = repository.getCustomItemTypesLastUpdatedDate()
        if (loginResponse?.userDetails != null) {
            headers["Authorization"] = "Bearer " + loginResponse.userDetails?.authtoken
        }
        val customTypes: Call<CustomItemTypesResponse> = projectsApi.getCustomItemsTypes(headers)
        customTypes.enqueue(object : AbstractCallback<CustomItemTypesResponse>() {
            override fun handleFailure(
                call: Call<CustomItemTypesResponse>?,
                throwable: Throwable?
            ) {

            }

            override fun handleError(
                call: Call<CustomItemTypesResponse>?,
                errorResponse: ErrorResponse?
            ) {

            }

            override fun handleSuccess(response: Response<CustomItemTypesResponse>?) {
                if (response != null) {
                    response.body()?.data?.let { repository.addCustomItemTypes(it.trackingItemTypes) }
                } else {
                }
            }

        })
    }

    fun getSections(): MutableList<IssueTrackingSectionCache>? {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        return loginResponse?.userDetails?.let { repository.getSections(it.users_id) }
    }

    fun getItems(sectionId: Int): MutableList<IssueTrackingItemsCache>? {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )

        return repository.getItems(sectionId,loginResponse?.userDetails?.users_id)
    }

    fun getItemTypes(trackingItemTypesId: Int): IssueTrackingItemTypesCache? {
        return repository.getItemTypes(trackingItemTypesId)
    }

    fun getValues(
        sectionId: Int,
        issueTrackingItemId: Int,
        pjIssueId: Long,
        pjIssueIdMobile: Long
    ): String? {
        return repository.getValues(sectionId, issueTrackingItemId, pjIssueId, pjIssueIdMobile)
    }

    fun getAdditionalData(trackingItemTypesId: Int?): String? {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
         loginResponse?.userDetails?.let{
return             repository.getAdditionalData(trackingItemTypesId,it.users_id)
         }
        return null
    }

    enum class MobileIdType {
        ISSUE, IMPACTS_ROOT_CAUSE, ITEM_BREAKDOWN, CUSTOM_FIELD
    }
}
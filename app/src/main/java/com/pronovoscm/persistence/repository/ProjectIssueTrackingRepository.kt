package com.pronovoscm.persistence.repository

import android.content.Context
import com.google.gson.Gson
import com.pronovoscm.PronovosApplication
import com.pronovoscm.chipslayoutmanager.util.log.Log
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.model.SyncDataEnum
import com.pronovoscm.model.TransactionLogUpdate
import com.pronovoscm.model.TransactionModuleEnum
import com.pronovoscm.model.response.issueTracking.impactAndRootCause.ImpactAndRootCause
import com.pronovoscm.model.response.issueTracking.issues.Assignee
import com.pronovoscm.model.response.issueTracking.issues.IssueItemBreakdown
import com.pronovoscm.model.response.issueTracking.issues.IssueTracking
import com.pronovoscm.model.response.issueTracking.issues.IssuesImpactAndRootCause
import com.pronovoscm.model.response.issueTracking.issues.PjTrackingItems
import com.pronovoscm.model.response.issueTracking.issues.PjTrackingSections
import com.pronovoscm.model.response.issueTracking.issues.TrackingItemTypes
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.model.view.CustomFields
import com.pronovoscm.model.view.IssueBreakdown
import com.pronovoscm.model.view.IssueImpactAndRootCause
import com.pronovoscm.model.view.IssueListItem
import com.pronovoscm.model.view.IssueListItemCustomFields
import com.pronovoscm.model.view.MasterImpactAndRootCause
import com.pronovoscm.persistence.domain.TransactionLogMobile
import com.pronovoscm.persistence.domain.TransactionLogMobileDao
import com.pronovoscm.persistence.domain.projectissuetracking.*
import com.pronovoscm.utils.DateFormatter
import com.pronovoscm.utils.SharedPref
import java.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.greendao.query.Query
import com.pronovoscm.model.request.issueTracking.IssueItemBreakdown as IssueItemBreakdownRequest
import com.pronovoscm.model.request.issueTracking.IssueTracking as IssueTrackingRequest
import com.pronovoscm.model.request.issueTracking.IssuesImpactAndRootCause as IssuesImpactAndRootCauseRequest


private const val DEFAULT_DATE = "1970-01-01 01:01:01"

open class ProjectIssueTrackingRepository(
    private val context: Context,
    private val impactsAndRootCauseCacheDao: ImpactsAndRootCauseCacheDao,
    private val projectsIssuesCacheDao: ProjectIssuesCacheDao,
    private val projectsImpactsAndCausesCacheDao: ProjectIssueImpactsAndCausesCacheDao,
    private val projectsItemBreakdownCacheDao: ProjectIssuesItemBreakdownCacheDao,
    private val transactionLogMobileDao: TransactionLogMobileDao,
    private val issueTrackingSectionDao: IssueTrackingSectionCacheDao,
    private val issueTrackingItemsCacheDao: IssueTrackingItemsCacheDao,
    private val issueTrackingItemTypesCacheDao: IssueTrackingItemTypesCacheDao,
    private val issueTrackingCustomFieldsCacheDao: IssueTrackingCustomFieldsCacheDao
) {

    fun addImpactsAndRootCause(impactAndRootCauseList: List<ImpactAndRootCause>) {
        try {
//                            deleteImpactsAndRootCause()
            impactAndRootCauseList.forEach { impactAndRootCause ->
//                deleteImpactsAndRootCause(impactAndRootCause)
                val impactAndRootCauseCache = impactsAndRootCauseCacheDao.queryBuilder()?.where(
                    ImpactsAndRootCauseCacheDao.Properties.PjIssuesCauseImpactId.eq(
                        impactAndRootCause.pjIssueCauseImpactId
                    )
                )?.unique()

                impactAndRootCauseCache?.let { safeCache ->

                    if (safeCache.updatedAt != null && impactAndRootCause.updatedAt != null) {
                        val localUpdateDate =
                            DateFormatter.getDateFromDateTimeString(safeCache.updatedAt)
                        val serverUpdateDate =
                            DateFormatter.getDateFromDateTimeString(impactAndRootCause.updatedAt)

                        if (localUpdateDate.before(serverUpdateDate)) {
                            impactsAndRootCauseCacheDao.updateInTx(
                                impactAndRootCause.toCache(
                                    cacheId = safeCache.cacheId
                                )
                            )
                        }
                    }

                } ?: run {
                    impactsAndRootCauseCacheDao.insertInTx(impactAndRootCause.toCache())
                }
            }

        } catch (e: Exception) {
            impactsAndRootCauseCacheDao.deleteAll()
            impactsAndRootCauseCacheDao.insertInTx(impactAndRootCauseList.toCache())
        }
    }

    fun deleteImpactsAndRootCause() {
        impactsAndRootCauseCacheDao.queryBuilder().buildDelete()
            .executeDeleteWithoutDetachingEntities()
        /*projectsImpactsAndCausesCacheDao
            .queryBuilder()
            .where(ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesId.eq(impactAndRootCause.pjIssueCauseImpactId))
            .buildDelete()
            .executeDeleteWithoutDetachingEntities()*/
    }

    fun getImpactAndRootCauseIcon(pjIssueCauseImpactId: Long): String =
        impactsAndRootCauseCacheDao.queryBuilder().let { queryBuilder ->
            val cache = queryBuilder.where(
                ImpactsAndRootCauseCacheDao.Properties.PjIssuesCauseImpactId.eq(
                    pjIssueCauseImpactId
                )
            ).build().unique()
            if (cache == null) return ""
            if (cache?.iconStoragePath.isNullOrEmpty()) {
                cache.mobileIcon
            } else {
                cache.iconStoragePath
            }
        }

    fun getImpactAndRootCauseName(pjIssueCauseImpactId: Long): String =
        impactsAndRootCauseCacheDao.queryBuilder().let { queryBuilder ->
            val cache = queryBuilder.where(
                ImpactsAndRootCauseCacheDao.Properties.PjIssuesCauseImpactId.eq(
                    pjIssueCauseImpactId
                )
            ).build().unique()
            if (cache == null) return ""
            cache.name
        }

    fun updateImpactAndRootCauseIconPath(
        iconStoragePath: String, pjIssueCauseImpactId: Long
    ) {
        impactsAndRootCauseCacheDao.queryBuilder().let { queryBuilder ->
            val cachedList = queryBuilder.where(
                ImpactsAndRootCauseCacheDao.Properties.PjIssuesCauseImpactId.eq(
                    pjIssueCauseImpactId
                )
            ).build().list()

            cachedList.forEach { impactsAndRootCauseCache ->
                impactsAndRootCauseCache.apply {
                    this.iconStoragePath = iconStoragePath
                }
            }
            impactsAndRootCauseCacheDao.updateInTx(cachedList)
        }
    }

    fun getImpactAndRootCause(): List<MasterImpactAndRootCause> =
        impactsAndRootCauseCacheDao.loadAll().toView()

    fun addIssues(issuesList: List<IssueTracking>, usersId: Int) {
        issuesList.forEach { issue ->
            Log.e("--------1", issue.customField.toString())

            var issueCache: ProjectIssuesCache? = null

            try {

                //New
                issueCache = projectsIssuesCacheDao.queryBuilder()
                    ?.where(ProjectIssuesCacheDao.Properties.PjIssueId.eq(issue.pjIssueId))
                    ?.unique()

                if (issueCache == null && issue.pjIssueIdMobile != 0L) {
                    issueCache = projectsIssuesCacheDao.queryBuilder()?.where(
                        ProjectIssuesCacheDao.Properties.PjIssueId.eq(0),
                        ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(issue.pjIssueIdMobile)
                    )?.unique()
                }
                Log.e("--------", issue.customField.toString())
                issueCache?.let { safeCache ->

                    deleteCustomFields(issueCache.pjIssueId, issueCache.pjIssueIdMobile)
                    projectsIssuesCacheDao.updateInTx(
                        issue.toCache(
                            cacheId = safeCache.cacheId
                        )
                    )

                    addCustomFields(issue, usersId = usersId)

                    projectsImpactsAndCausesCacheDao.queryBuilder().where(
                        ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesId.eq(
                            issueCache.pjIssueId
                        ), ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesIdMobile.eq(
                            issueCache.pjIssueIdMobile
                        )
                    ).buildDelete().executeDeleteWithoutDetachingEntities()

                    projectsImpactsAndCausesCacheDao.insertInTx(
                        (issue.impacts?.toCache(
                            pjIssuesIdMobile = issue.pjIssueIdMobile ?: 0L,
                            projectId = issue.pjProjectsId,
                            usersId = issue.usersId
                        ) ?: emptyList()) + (issue.rootCause?.toCache(
                            pjIssuesIdMobile = issue.pjIssueIdMobile ?: 0L,
                            projectId = issue.pjProjectsId,
                            usersId = issue.usersId
                        ) ?: emptyList())
                    )

                    projectsItemBreakdownCacheDao.queryBuilder().where(
                        ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesId.eq(
                            issueCache.pjIssueId
                        ), ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesIdMobile.eq(
                            issueCache.pjIssueIdMobile
                        )
                    ).buildDelete().executeDeleteWithoutDetachingEntities()

                    projectsItemBreakdownCacheDao.insertInTx(
                        issue.itemsBreakdown?.toCache(
                            pjIssuesIdMobile = issue.pjIssueIdMobile ?: 0L,
                            projectId = issue.pjProjectsId,
                            usersId = issue.usersId
                        )
                    )

                } ?: run {
                    if (issue.deletedAt.isNullOrEmpty()) {
                        val cache = issue.toCache()
                        projectsIssuesCacheDao.insertInTx(cache)
                        projectsItemBreakdownCacheDao.insertInTx(cache.breakdownCacheList)
                        projectsImpactsAndCausesCacheDao.insertInTx(cache.impactsAndCausesCacheList)
                        deleteCustomFields(cache.pjIssueId, cache.pjIssueIdMobile)
                        addCustomFields(issue, usersId)
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()

                projectsImpactsAndCausesCacheDao.queryBuilder().where(
                    ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesId.eq(issueCache?.pjIssueId),
                    ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesIdMobile.eq(
                        issueCache?.pjIssueIdMobile
                    )
                ).buildDelete().executeDeleteWithoutDetachingEntities()

                projectsItemBreakdownCacheDao.queryBuilder().where(
                    ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesId.eq(issueCache?.pjIssueId),
                    ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesIdMobile.eq(issueCache?.pjIssueIdMobile)
                ).buildDelete().executeDeleteWithoutDetachingEntities()

                projectsIssuesCacheDao.queryBuilder().where(
                    ProjectIssuesCacheDao.Properties.PjIssueId.eq(issueCache?.pjIssueId),
                    ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(issueCache?.pjIssueIdMobile)
                ).buildDelete().executeDeleteWithoutDetachingEntities()

                val newCache = issue.toCache()

                projectsIssuesCacheDao.insertInTx(newCache)
                projectsItemBreakdownCacheDao.insertInTx(newCache.breakdownCacheList)
                projectsImpactsAndCausesCacheDao.insertInTx(newCache.impactsAndCausesCacheList)
            }
        }
    }

    private fun deleteCustomFields(pjIssueId: Long, pjIssueIdMobile: Long?) {
        if (pjIssueId != 0L) {
            val delete = issueTrackingCustomFieldsCacheDao.queryBuilder().where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingId.eq(pjIssueId),
            ).buildDelete()
            delete.executeDeleteWithoutDetachingEntities()
        } else {
            val delete = issueTrackingCustomFieldsCacheDao.queryBuilder().where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingMobileId.eq(
                    pjIssueIdMobile
                ),
            ).buildDelete()
            delete.executeDeleteWithoutDetachingEntities()
        }
    }

    private fun addCustomFields(issue: IssueTracking, usersId: Int) {

        if (issue.customField.isNullOrEmpty().not()) {
            issue.customField?.forEach { issuesItemCustomFields ->
                val dataToInsert = IssueTrackingCustomFieldsCache()
                dataToInsert.issueTrackingCustomId = issuesItemCustomFields.issue_tracking_custom_id
                dataToInsert.issueTrackingCustomMobileId =
                    issuesItemCustomFields.issue_tracking_custom_mobile_id
                dataToInsert.issueTrackingId = issuesItemCustomFields.issue_tracking_id
                dataToInsert.issueTrackingMobileId = issuesItemCustomFields.issue_tracking_mobile_id
                dataToInsert.issueSectionId = issuesItemCustomFields.issue_section_id
                dataToInsert.issueTrackingItemsId = issuesItemCustomFields.issue_tracking_items_id
                dataToInsert.trackingItemTypesId = issuesItemCustomFields.tracking_item_types_id
                dataToInsert.value = issuesItemCustomFields.value
                dataToInsert.tenantId = issue.tenantId
                dataToInsert.usersId = usersId.toLong()
                dataToInsert.isSync = false

                issueTrackingCustomFieldsCacheDao.insertOrReplace(dataToInsert)
            }
        }
    }

    private fun updateCustomFields(
        customFieldValues: ArrayList<CustomFields>, issueListItem: IssueListItem
    ) {

        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        customFieldValues.forEach { issuesItemCustomFields ->
            val dataToInsert = IssueTrackingCustomFieldsCache()
            dataToInsert.issueTrackingCustomId = 0
            dataToInsert.issueTrackingCustomMobileId =
                generateMobileId(ProjectIssueTrackingProvider.MobileIdType.CUSTOM_FIELD)
            dataToInsert.issueTrackingId = issueListItem.pjIssueId
            dataToInsert.issueTrackingMobileId = issueListItem.pjIssueIdMobile
            dataToInsert.issueSectionId = issuesItemCustomFields.issueSectionId
            dataToInsert.issueTrackingItemsId = issuesItemCustomFields.issueTrackingItemsId
            dataToInsert.trackingItemTypesId = issuesItemCustomFields.trackingItemTypesId
            dataToInsert.value = issuesItemCustomFields.value
            dataToInsert.tenantId = issueListItem.tenantId
            dataToInsert.usersId = (loginResponse?.userDetails?.users_id ?: 0L).toLong()
            dataToInsert.isSync = false
            issueTrackingCustomFieldsCacheDao.insert(dataToInsert)

        }
    }

    fun addIssue(
        issueListItem: IssueListItem, customFieldValues: ArrayList<CustomFields>
    ): IssueListItem? {

        val issueCache = issueListItem.toCache()
        return try {
            projectsIssuesCacheDao.insertInTx(issueCache)
            projectsItemBreakdownCacheDao.insertInTx(issueCache.breakdownCacheList)
            projectsImpactsAndCausesCacheDao.insertInTx(issueCache.impactsAndCausesCacheList)
            val loginResponse: LoginResponse? = Gson().fromJson(
                SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse::class.java
            )
            customFieldValues.forEach { issuesItemCustomFields ->
                val dataToInsert = IssueTrackingCustomFieldsCache()
                dataToInsert.issueTrackingCustomId = 0
                dataToInsert.issueTrackingCustomMobileId =
                    generateMobileId(ProjectIssueTrackingProvider.MobileIdType.CUSTOM_FIELD)
                dataToInsert.issueTrackingId = issueCache.pjIssueId
                dataToInsert.issueTrackingMobileId = issueCache.pjIssueIdMobile
                dataToInsert.issueSectionId = issuesItemCustomFields.issueSectionId
                dataToInsert.issueTrackingItemsId = issuesItemCustomFields.issueTrackingItemsId
                dataToInsert.trackingItemTypesId = issuesItemCustomFields.trackingItemTypesId
                dataToInsert.value = issuesItemCustomFields.value
                dataToInsert.tenantId = issueCache.tenantId
                dataToInsert.usersId = (loginResponse?.userDetails?.users_id ?: 0).toLong()
                dataToInsert.isSync = false
                issueTrackingCustomFieldsCacheDao.insert(dataToInsert)
            }
            transactionLogMobileDao.save(
                TransactionLogMobile(
                    loginResponse?.userDetails?.users_id,
                    TransactionModuleEnum.ISSUE_TRACKING.ordinal,
                    SyncDataEnum.NOTSYNC.ordinal,
                    issueCache.pjIssueIdMobile,
                    issueCache.pjIssueId,
                    null,
                    DateFormatter.getDateFromDateTimeString(issueCache.createdAt)
                )
            )

            PronovosApplication.getContext()?.setupAndStartWorkManager()

            return getIssueDetails(issueListItem.pjIssueId, issueListItem.pjIssueIdMobile)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateIssue(
        issueListItem: IssueListItem, customFieldValues: ArrayList<CustomFields>
    ): IssueListItem? {

        return try {

            val currentCache = projectsIssuesCacheDao.queryBuilder()
                ?.where(ProjectIssuesCacheDao.Properties.CacheId.eq(issueListItem.cacheId))
                ?.unique()
            deleteCustomFields(issueListItem.pjIssueId, issueListItem.pjIssueIdMobile)
            val cacheIssue = issueListItem.toCache(
                cacheId = currentCache?.cacheId
            )

            updateCustomFields(customFieldValues, issueListItem)

            projectsImpactsAndCausesCacheDao.queryBuilder().where(
                ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesId.eq(currentCache?.pjIssueId),
                ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesIdMobile.eq(currentCache?.pjIssueIdMobile)
            ).buildDelete().executeDeleteWithoutDetachingEntities()

            projectsImpactsAndCausesCacheDao.insertInTx(cacheIssue.impactsAndCausesCacheList)

            projectsItemBreakdownCacheDao.queryBuilder().where(
                ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesId.eq(currentCache?.pjIssueId),
                ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesIdMobile.eq(currentCache?.pjIssueIdMobile)
            ).buildDelete().executeDeleteWithoutDetachingEntities()

            projectsItemBreakdownCacheDao.insertInTx(cacheIssue.breakdownCacheList)

            projectsIssuesCacheDao.update(cacheIssue)

            val loginResponse: LoginResponse? = Gson().fromJson(
                SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse::class.java
            )

            transactionLogMobileDao.save(
                TransactionLogMobile(
                    loginResponse?.userDetails?.users_id,
                    TransactionModuleEnum.ISSUE_TRACKING.ordinal,
                    SyncDataEnum.NOTSYNC.ordinal,
                    cacheIssue.pjIssueIdMobile,
                    cacheIssue.pjIssueId,
                    null,
                    Date()
                )
            )

            PronovosApplication.getContext()?.setupAndStartWorkManager()

            return getIssueDetails(cacheIssue.pjIssueId, cacheIssue.pjIssueIdMobile)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getIssues(projectId: Long) = projectsIssuesCacheDao.queryBuilder().let { queryBuilder ->
        queryBuilder?.where(
            ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId),
            ProjectIssuesCacheDao.Properties.DeletedAt.isNull
        )?.orderAsc(
            ProjectIssuesCacheDao.Properties.IsResolved,
            ProjectIssuesCacheDao.Properties.PjIssueId
        )?.list()?.map { cacheIssue ->
            cacheIssue.toView()
        }
    }

    fun getIssueDetails(pjIssueId: Long, pjIssuesIdMobile: Long): IssueListItem? =
        projectsIssuesCacheDao.queryBuilder()?.let { queryBuilder ->
            queryBuilder.where(
                ProjectIssuesCacheDao.Properties.PjIssueId.eq(pjIssueId),
                ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(pjIssuesIdMobile)
            ).build().unique()?.toView(includedDeleted = true)
        }

    fun isValidMobileId(mobileId: Long, type: ProjectIssueTrackingProvider.MobileIdType) =
        when (type) {
            ProjectIssueTrackingProvider.MobileIdType.ISSUE -> {
                projectsIssuesCacheDao.queryBuilder()
                    .where(ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(mobileId)).list()
                    .isEmpty()
            }

            ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE -> {
                projectsImpactsAndCausesCacheDao.queryBuilder().where(
                    ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesTrackingIdMobile.eq(
                        mobileId
                    )
                ).list().isEmpty()
            }

            ProjectIssueTrackingProvider.MobileIdType.ITEM_BREAKDOWN -> {
                projectsItemBreakdownCacheDao.queryBuilder().where(
                    ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesItemBreakdownIdMobile.eq(
                        mobileId
                    )
                ).list().isEmpty()
            }
            ProjectIssueTrackingProvider.MobileIdType.CUSTOM_FIELD -> {
                issueTrackingCustomFieldsCacheDao.queryBuilder().where(
                    IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingCustomMobileId.eq(
                        mobileId
                    )
                ).list().isEmpty()
            }
            ProjectIssueTrackingProvider.MobileIdType.CUSTOM_FIELD -> {
                issueTrackingCustomFieldsCacheDao.queryBuilder().where(
                    IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingCustomMobileId.eq(
                        mobileId
                    )
                ).list().isEmpty()
            }
        }

    fun generateMobileId(type: ProjectIssueTrackingProvider.MobileIdType): Long {
        val timeSeed = System.nanoTime() // to get the current date time value

        val randSeed = Math.random() * 1000 // random number generation

        var mobileId = (timeSeed * randSeed).toLong()

        val s = mobileId.toString() + ""
        val subStr = s.substring(0, 9)
        mobileId = subStr.toLong()

        isValidMobileId(mobileId, type)

        if (isValidMobileId(mobileId, type).not()) {
            generateMobileId(type)
        }

        return mobileId
    }

    fun getImpactAndRootCauseLastUpdateDate(): String {
        return DEFAULT_DATE
    }

    fun getProjectIssuesLastUpdateDate(projectId: Long?) = projectId?.let { mProjectId ->
        projectsIssuesCacheDao.queryBuilder()
            ?.where(ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId))
            ?.orderDesc(ProjectIssuesCacheDao.Properties.UpdatedAt)?.limit(1)?.unique()?.updatedAt
            ?: ""
    } ?: DEFAULT_DATE

    fun getSectionLastUpdatedDate(): String {
        val loginResponse = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val maxPostIdRow: List<IssueTrackingSectionCache> =
            issueTrackingSectionDao.queryBuilder().where(
                IssueTrackingSectionCacheDao.Properties.UpdatedAt.isNotNull,
                IssueTrackingSectionCacheDao.Properties.UsersId.eq(loginResponse.userDetails.users_id)
            ).orderDesc(IssueTrackingSectionCacheDao.Properties.UpdatedAt).limit(1).list()
        if (maxPostIdRow.size > 0) {
            val maxUpdatedAt = maxPostIdRow[0].updatedAt
            return DateFormatter.formatDateTimeForService(maxUpdatedAt)
        }
        return DEFAULT_DATE
    }

    fun getCustomItemsLastUpdatedDate(): String {
        val loginResponse = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val maxPostIdRow: List<IssueTrackingItemsCache> =
            issueTrackingItemsCacheDao.queryBuilder().where(
                IssueTrackingItemsCacheDao.Properties.Updated_at.isNotNull,
                IssueTrackingItemsCacheDao.Properties.UsersId.eq(loginResponse.userDetails.users_id)
            ).orderDesc(IssueTrackingItemsCacheDao.Properties.Updated_at).limit(1).list()
        if (maxPostIdRow.size > 0) {
            val maxUpdatedAt = maxPostIdRow[0].updated_at
            return DateFormatter.formatDateTimeForService(maxUpdatedAt)
        }
        return DEFAULT_DATE
    }

    fun getCustomItemTypesLastUpdatedDate(): String {

        val maxPostIdRow: List<IssueTrackingItemTypesCache> =
            issueTrackingItemTypesCacheDao.queryBuilder().where(
                IssueTrackingItemTypesCacheDao.Properties.UpdatedAt.isNotNull
            ).orderDesc(IssueTrackingItemTypesCacheDao.Properties.UpdatedAt).limit(1).list()
        if (maxPostIdRow.size > 0) {
            val maxUpdatedAt = maxPostIdRow[0].updatedAt
            return DateFormatter.formatDateTimeForService(maxUpdatedAt)
        }
        return DEFAULT_DATE
    }

    fun deleteIssue(pjIssueId: Long, pjIssueIdMobile: Long) {

        if (pjIssueId != 0L) {

            val issue = projectsIssuesCacheDao.queryBuilder()?.where(
                ProjectIssuesCacheDao.Properties.PjIssueId.eq(pjIssueId),
                ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(pjIssueIdMobile)
            )?.unique()


            projectsIssuesCacheDao.update(issue?.apply {
                deletedAt = DateFormatter.formatDateTimeForService(Date())
                isSync = false
            })

            val loginResponse: LoginResponse? = Gson().fromJson(
                SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse::class.java
            )

            transactionLogMobileDao.save(
                TransactionLogMobile(
                    loginResponse?.userDetails?.users_id,
                    TransactionModuleEnum.ISSUE_TRACKING.ordinal,
                    SyncDataEnum.NOTSYNC.ordinal,
                    issue?.pjIssueIdMobile ?: 0,
                    issue?.pjIssueId ?: 0,
                    null,
                    Date()
                )
            )
            val transactionLogUpdate = TransactionLogUpdate()
            transactionLogUpdate.transactionModuleEnum = TransactionModuleEnum.ISSUE_TRACKING
            EventBus.getDefault().post(transactionLogUpdate)

            PronovosApplication.getContext()?.setupAndStartWorkManager()
        } else {

            projectsIssuesCacheDao.queryBuilder().where(
                ProjectIssuesCacheDao.Properties.PjIssueId.eq(pjIssueId),
                ProjectIssuesCacheDao.Properties.PjIssueIdMobile.eq(pjIssueIdMobile)
            ).buildDelete().executeDeleteWithoutDetachingEntities()

            projectsImpactsAndCausesCacheDao.queryBuilder().where(
                ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesId.eq(pjIssueId),
                ProjectIssueImpactsAndCausesCacheDao.Properties.PjIssuesIdMobile.eq(
                    pjIssueIdMobile
                )
            ).buildDelete().executeDeleteWithoutDetachingEntities()

            projectsItemBreakdownCacheDao.queryBuilder().where(
                ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesId.eq(pjIssueId),
                ProjectIssuesItemBreakdownCacheDao.Properties.PjIssuesIdMobile.eq(
                    pjIssueIdMobile
                )
            ).buildDelete().executeDeleteWithoutDetachingEntities()

        }

    }

    fun filterIssueList(
        searchQuery: String?, status: Boolean?, rootCauseAndImpactId: List<Long>, projectId: Long
    ): List<IssueListItem> {

        val queryBuilder = projectsIssuesCacheDao.queryBuilder()

        val issueIds = if (rootCauseAndImpactId.isNotEmpty()) {

            val rawQuery = if (rootCauseAndImpactId.size == 1) {
                "SELECT * FROM pj_issue_impacts_and_causes" + " WHERE project_id = $projectId AND pj_issues_causeimpact_id=${rootCauseAndImpactId[0]} AND deleted_at IS NULL"
            } else {
                "SELECT im.* FROM pj_issue_impacts_and_causes im" + " JOIN pj_issue_impacts_and_causes ir" + " ON  im.pj_issues_id = ir.pj_issues_id AND im.pj_issues_id_mobile = ir.pj_issues_id_mobile" + " WHERE im.project_id = $projectId AND im.pj_issues_causeimpact_id=${rootCauseAndImpactId[0]} AND ir.pj_issues_causeimpact_id=${rootCauseAndImpactId[1]} AND im.deleted_at IS NULL AND ir.deleted_at IS NULL"
            }

            val query: Query<ProjectIssueImpactsAndCausesCache> = Query.internalCreate(
                projectsImpactsAndCausesCacheDao, rawQuery, arrayOf<String>()
            )

            query.list()

        } else emptyList()

        val filteredList = if (status != null) {
            if (rootCauseAndImpactId.isNotEmpty()) {
                queryBuilder.where(
                    ProjectIssuesCacheDao.Properties.IsResolved.eq(status),
                    ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId),
                    ProjectIssuesCacheDao.Properties.PjIssueId.`in`(issueIds.map { it.pjIssuesId }),
                    ProjectIssuesCacheDao.Properties.PjIssueIdMobile.`in`(issueIds.map { it.pjIssuesIdMobile })
                )?.orderAsc(
                    ProjectIssuesCacheDao.Properties.IsResolved,
                    ProjectIssuesCacheDao.Properties.PjIssueId
                )?.list()
            } else {
                queryBuilder.where(
                    ProjectIssuesCacheDao.Properties.IsResolved.eq(status),
                    ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId)
                )?.orderAsc(
                    ProjectIssuesCacheDao.Properties.IsResolved,
                    ProjectIssuesCacheDao.Properties.PjIssueId
                )?.list()
            }
        } else if (rootCauseAndImpactId.isNotEmpty()) {
            queryBuilder.where(
                ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId),
                ProjectIssuesCacheDao.Properties.PjIssueId.`in`(issueIds.map { it.pjIssuesId }),
                ProjectIssuesCacheDao.Properties.PjIssueIdMobile.`in`(issueIds.map { it.pjIssuesIdMobile })
            )?.orderAsc(
                ProjectIssuesCacheDao.Properties.IsResolved,
                ProjectIssuesCacheDao.Properties.PjIssueId
            )?.list()
        } else {
            queryBuilder?.where(ProjectIssuesCacheDao.Properties.PjProjectsId.eq(projectId))
                ?.orderAsc(
                    ProjectIssuesCacheDao.Properties.IsResolved,
                    ProjectIssuesCacheDao.Properties.PjIssueId
                )?.list()
        }

        val searchedList = searchQuery?.let { query ->

            filteredList?.filter { cache ->
                cache.title.contains(query, ignoreCase = true)
            }

        } ?: filteredList

        return searchedList?.filter { it.deletedAt.isNullOrEmpty() }?.map {
            it.toView()
        } ?: emptyList()
    }

    fun List<ImpactAndRootCause>.toCache() = map { impactAndRootCause ->
        impactAndRootCause.toCache()
    }

    fun getUnSyncedIssues() = projectsIssuesCacheDao.let { issuesDao ->
        issuesDao.queryBuilder()?.where(
            ProjectIssuesCacheDao.Properties.IsSync.eq(false),
            ProjectIssuesCacheDao.Properties.IsInProcess.eq(false)
        )?.list()?.map { issue ->
            issue.breakdownCacheList.removeAll {
                it.pjIssuesItemBreakdownId == 0L && it.deletedAt.isNullOrEmpty().not()
            }
            issue.impactsAndCausesCacheList.removeAll {
                it.pjIssuesTrackingsId == 0L && it.deletedAt.isNullOrEmpty().not()
            }
            issue
        }?.toRequest()
    }

    fun updateIssueProcessStatus(
        mobileIds: List<Long>, inProcess: Boolean
    ) {
        projectsIssuesCacheDao.queryBuilder()?.where(
            ProjectIssuesCacheDao.Properties.PjIssueIdMobile.`in`(mobileIds),
            ProjectIssuesCacheDao.Properties.IsInProcess.eq(inProcess.not()),
            ProjectIssuesCacheDao.Properties.IsSync.eq(false)
        )

        projectsIssuesCacheDao.queryBuilder().list().map { cache ->
            cache.inProcess = inProcess
            projectsIssuesCacheDao.save(cache)
        }
    }

    fun updateIssueSyncStatus(
        mobileIds: List<Long>, isSync: Boolean
    ) {
        projectsIssuesCacheDao.queryBuilder()?.where(
            ProjectIssuesCacheDao.Properties.PjIssueIdMobile.`in`(mobileIds)
        )?.list()?.map { cache ->
            cache.sync = isSync
            projectsIssuesCacheDao.save(cache)
        }
    }

    fun updateMobileLogSyncStatus(
        mobileIds: List<Long>,
        status: SyncDataEnum,
    ) {
        transactionLogMobileDao.queryBuilder()
            ?.where(TransactionLogMobileDao.Properties.MobileId.`in`(mobileIds))?.or(
                TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.SYNC_FAILED),
                TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC)
            )

        transactionLogMobileDao.queryBuilder().list().map { cache ->
            cache.status = status.ordinal
            transactionLogMobileDao.save(cache)
        }
    }

    fun deleteMobileLogs(
        mobileIds: List<Long>
    ) {
        transactionLogMobileDao.queryBuilder()?.where(
            TransactionLogMobileDao.Properties.MobileId.`in`(mobileIds),
            TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.ISSUE_TRACKING.ordinal)
        )?.buildDelete()?.executeDeleteWithoutDetachingEntities()

    }

    fun ImpactAndRootCause.toCache(
        cacheId: Long? = null,
    ) = ImpactsAndRootCauseCache(
        /* cacheId = */ cacheId,
        /* pjIssueCauseImpactId = */ pjIssueCauseImpactId,
        /* name = */ name,
        /* impactStatus = */ impactStatus,
        /* createdAt = */ createdAt,
        /* updatedAt = */ updatedAt,
        /* iconUrl = */ iconUrl,
        /* mobileIcon = */ mobileIcon,
        /* iconStoragePath = */ ""
    )

    fun List<ImpactsAndRootCauseCache>.toView() = map { impactsAndRootCauseCache ->
        impactsAndRootCauseCache.toView()
    }

    fun ImpactsAndRootCauseCache.toView() = MasterImpactAndRootCause(
        pjIssueCauseImpactId = pjIssuesCauseImpactId,
        name = name,
        impactStatus = impactStatus,
        iconUrl = getImpactAndRootCauseIcon(pjIssuesCauseImpactId)
    )

    @JvmName("toCacheIssueTracking")
    fun List<IssueTracking>.toCache() = map { issue ->
        issue.toCache()
    }

    fun IssueTracking.toCache(
        cacheId: Long? = null
    ) = ProjectIssuesCache(
        /* cacheId = */
        cacheId,
        /* pjIssueId = */
        pjIssueId,
        /* pjProjectsId = */
        pjProjectsId,
        /* usersId = */
        usersId,
        /* tenantId = */
        tenantId,
        /* issueNumber = */
        issueNumber,
        /* title = */
        title,
        /* dateCreated = */
        dateCreated,
        /* dateResolved = */
        dateResolved,
        /* isResolved = */
        resolvedStatus == 1,
        /* description = */
        description,
        /* pjIssueIdMobile = */
        pjIssueIdMobile,
        /* createdAt = */
        createdAt,
        /* updatedAt = */
        updatedAt,
        /* deletedAt = */
        deletedAt,
        /* createdBy = */
        issueCreatedBy.usersId,
        /* isInProcess = */
        false,
        /* isSync = */
        true,
        /* createdByName = */
        issueCreatedBy.fullName,
        neededBy,
        assignee?.assigneeId ?: 0,
        assignee?.assigneeName,
        neededByTimezone
    ).also { issuesCache ->
        issuesCache.impactsAndCausesCacheList = (impacts?.toCache(
            pjIssuesIdMobile = pjIssueIdMobile ?: 0, projectId = pjProjectsId, usersId = usersId
        ) ?: emptyList()) + (rootCause?.map {
            it.toCache(
                pjIssuesIdMobile = pjIssueIdMobile ?: 0, projectId = pjProjectsId, usersId = usersId
            )
        } ?: emptyList())

        issuesCache.breakdownCacheList = itemsBreakdown?.toCache(
            pjIssuesIdMobile = pjIssueIdMobile ?: 0, projectId = pjProjectsId, usersId = usersId
        )
    }

    @JvmName("toCacheIssuesImpactAndRootCause")
    fun List<IssuesImpactAndRootCause>.toCache(
        pjIssuesIdMobile: Long, projectId: Long, usersId: Long
    ) = filter {
        it.deletedAt.isNullOrEmpty()
    }.map { issuesImpactAndRootCause ->
        issuesImpactAndRootCause.toCache(
            pjIssuesIdMobile = pjIssuesIdMobile, projectId = projectId, usersId = usersId
        )
    }

    fun IssuesImpactAndRootCause.toCache(
        cacheId: Long? = null, pjIssuesIdMobile: Long, projectId: Long, usersId: Long
    ) = ProjectIssueImpactsAndCausesCache(
        /* cacheId = */ cacheId,
        /* pjIssuesTrackingsId = */ pjIssuesTrackingsId,
        /* pjIssuesId = */ pjIssuesId,
        /* pjIssuesIdMobile = */ pjIssuesIdMobile,
        /* pjIssuesCauseImpactId = */ pjIssuesCauseImpactId,
        /* type = */ type,
        /* pjIssuesTrackingIdMobile = */ pjIssuesTrackingIdMobile,
        /* userId = */ usersId,
        /* projectId = */ projectId,
        /* createdAt = */ createdAt,
        /* updatedAt = */ updatedAt,
        /* deletedAt = */ deletedAt
    )

    fun List<IssueItemBreakdown>.toCache(
        pjIssuesIdMobile: Long, projectId: Long, usersId: Long
    ) = filter {
        it.deletedAt.isNullOrEmpty()
    }.map { issueItemBreakdown ->
        issueItemBreakdown.toCache(
            pjIssuesIdMobile = pjIssuesIdMobile, projectId = projectId, userId = usersId
        )
    }

    fun IssueItemBreakdown.toCache(
        cacheId: Long? = null, pjIssuesIdMobile: Long, projectId: Long, userId: Long
    ) = ProjectIssuesItemBreakdownCache(
        /* cacheId = */ cacheId,
        /* pjIssuesItemBreakdownId = */ pjIssuesItemBreakdownId,
        /* pjIssuesId = */ pjIssuesId,
        /* pjIssuesIdMobile = */ pjIssuesIdMobile,
        /* description = */ description,
        /* days = */ days,
        /* amount = */ amount,
        /* pjIssuesItemBreakdownIdMobile = */ pjIssuesItemBreakdownIdMobile,
        /* userId = */ userId,
        /* projectId = */ projectId,
        /* createdAt = */ createdAt,
        /* updatedAt = */ updatedAt,
        /* deletedAt = */ deletedAt
    )

    fun IssueListItem.toCache(
        cacheId: Long? = null
    ) = ProjectIssuesCache(
        /* cacheId = */
        cacheId,
        /* pjIssueId = */
        pjIssueId,
        /* pjProjectsId = */
        pjProjectsId,
        /* usersId = */
        usersId,
        /* tenantId = */
        tenantId,
        /* issueNumber = */
        issueNumber,
        /* title = */
        title,
        /* dateCreated = */
        dateCreated,
        /* dateResolved = */
        dateResolved,
        /* isResolved = */
        resolvedStatus,
        /* description = */
        description,
        /* pjIssueIdMobile = */
        pjIssueIdMobile,
        /* createdAt = */
        createdAt,
        /* updatedAt = */
        updatedAt,
        /* deletedAt = */
        deletedAt,
        /* createdBy = */
        createdBy,
        /* isInProcess = */
        false,
        /* isSync = */
        false,
        /* createdByName = */
        createdByName, neededBy, assignee?.assigneeId ?: 0, assignee?.assigneeName, neededByTimeZone
    ).also { issuesCache ->
        issuesCache.impactsAndCausesCacheList = impactsAndRootCause.map { impactCause ->
            impactCause.toCache(
                pjIssuesIdMobile = pjIssueIdMobile,
                projectId = pjProjectsId,
                usersId = issuesCache.usersId,
                cacheId = impactCause.cacheId
            )
        }

        issuesCache.breakdownCacheList = issuesBreakdown.map { breakdown ->
            breakdown.toCache(
                pjIssuesIdMobile = pjIssueIdMobile,
                projectId = pjProjectsId,
                usersId = issuesCache.usersId,
                cacheId = breakdown.cacheId
            )
        }
    }

    fun IssueImpactAndRootCause.toCache(
        projectId: Long, pjIssuesIdMobile: Long, cacheId: Long? = null, usersId: Long
    ) = ProjectIssueImpactsAndCausesCache(
        /* cacheId = */ cacheId,
        /* pjIssuesTrackingsId = */ pjIssuesTrackingsId,
        /* pjIssuesId = */ pjIssuesId,
        /* pjIssuesIdMobile = */ pjIssuesIdMobile,
        /* pjIssuesCauseImpactId = */ pjIssuesCauseImpactId,
        /* type = */ type,
        /* pjIssuesTrackingIdMobile = */ pjIssuesTrackingIdMobile,
        /* userId = */  usersId,
        /* projectId = */ projectId,
        /* createdAt = */ createdAt,
        /* updatedAt = */ updatedAt,
        /* deletedAt = */ deletedAt
    )

    fun IssueBreakdown.toCache(
        cacheId: Long? = null, projectId: Long, pjIssuesIdMobile: Long, usersId: Long
    ) = ProjectIssuesItemBreakdownCache(
        /* cacheId = */ cacheId,
        /* pjIssuesItemBreakdownId = */ pjIssuesItemBreakdownId,
        /* pjIssuesId = */ pjIssuesId,
        /* pjIssuesIdMobile = */ pjIssuesIdMobile,
        /* description = */ description,
        /* days = */ days,
        /* amount = */ amount,
        /* pjIssuesItemBreakdownIdMobile = */ pjIssuesItemBreakdownIdMobile,
        /* userId = */ usersId,
        /* projectId = */ projectId,
        /* createdAt = */ createdAt,
        /* updatedAt = */ updatedAt,
        /* deletedAt = */ deletedAt
    )

    fun ProjectIssuesCache.toView(includedDeleted: Boolean = false) = IssueListItem(
        pjIssueId = pjIssueId,
        pjIssueIdMobile = pjIssueIdMobile,
        pjProjectsId = pjProjectsId,
        usersId = usersId,
        tenantId = tenantId,
        issueNumber = issueNumber,
        title = title,
        dateCreated = dateCreated,
        dateResolved = dateResolved,
        resolvedStatus = isResolved,
        description = description,
        impactsAndRootCause = impactsAndCausesCacheList.toView(includedDeleted),
        issuesBreakdown = breakdownCacheList.toView(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        createdBy = createdBy,
        createdByName = createdByName,
        cacheId = cacheId,
        neededBy = needeBy,
        assignee = Assignee(assigneId, assigneeName),
        neededByTimeZone = neededByTimezone
    )

    /*    fun ProjectIssuesCache.toDetailView() = IssueListItem(
                pjIssueId = pjIssueId,
                pjIssueIdMobile = pjIssueIdMobile,
                pjProjectsId = pjProjectsId,
                usersId = usersId,
                tenantId = tenantId,
                issueNumber = issueNumber,
                title = title,
                dateCreated = dateCreated,
                dateResolved = dateResolved,
                resolvedStatus = isResolved,
                description = description,
                impactsAndRootCause = impactsAndCausesCacheList.toView(),
                issuesBreakdown = breakdownCacheList.toView(),
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
        )*/

    fun List<ProjectIssueImpactsAndCausesCache>.toDetailsView() = let { impactsAndCausesCacheList ->

        getImpactAndRootCause()

        /* getImpactAndRootCause().map { masterImpactAndRootCause ->
             IssueImpactAndRootCause(
                     pjIssuesTrackingsId = masterImpactAndRootCause.pjIssueCauseImpactId,
                     pjIssuesId = masterImpactAndRootCause.pjIssuesId,
                     pjIssuesCauseImpactId = masterImpactAndRootCause.pjIssueCauseImpactId,
                     type = masterImpactAndRootCause.type,
                     pjIssuesTrackingIdMobile = masterImpactAndRootCause.pjIssuesTrackingIdMobile,
                     iconUrl = getImpactAndRootCauseIcon(masterImpactAndRootCause.pjIssuesCauseImpactId)
             )
         }*/
    }

    @JvmName("toViewProjectIssueImpactsAndCausesCache")
    fun List<ProjectIssueImpactsAndCausesCache>.toView(includedDeleted: Boolean = false) =
        mapNotNull { cache ->
            if (cache.deletedAt == null || includedDeleted) cache.toView()
            else null
        }

    fun ProjectIssueImpactsAndCausesCache.toView() = IssueImpactAndRootCause(
        pjIssuesTrackingsId = pjIssuesTrackingsId,
        pjIssuesId = pjIssuesId,
        pjIssuesCauseImpactId = pjIssuesCauseImpactId,
        type = type,
        pjIssuesTrackingIdMobile = pjIssuesTrackingIdMobile,
        iconUrl = getImpactAndRootCauseIcon(pjIssuesCauseImpactId),
        name = getImpactAndRootCauseName(pjIssuesCauseImpactId),
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        cacheId = cacheId
    )

    @JvmName("toViewProjectIssuesItemBreakdownCache")
    fun List<ProjectIssuesItemBreakdownCache>.toView() = mapIndexed { index, it ->
        it.toView().also {
            it.number = index + 1
        }
    }

    fun ProjectIssuesItemBreakdownCache.toView() = IssueBreakdown(
        pjIssuesItemBreakdownId = pjIssuesItemBreakdownId,
        pjIssuesItemBreakdownIdMobile = pjIssuesItemBreakdownIdMobile,
        pjIssuesId = pjIssuesId,
        pjIssuesIdMobile = pjIssuesIdMobile,
        description = description,
        days = days,
        amount = amount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        cacheId = cacheId
    )

    @JvmName("toRequestProjectIssuesCache")
    fun List<ProjectIssuesCache>.toRequest() = map {
        it.toRequest()
    }

    fun ProjectIssuesCache.toRequest() = IssueTrackingRequest(pjIssueId = pjIssueId,
        pjProjectsId = pjProjectsId,
        title = title,
        dateCreated = dateCreated,
        resolvedStatus = if (isResolved) 1 else 0,
        description = description,
        pjIssueIdMobile = pjIssueIdMobile,
        deletedAt = deletedAt?.let { deletedAt } ?: "",
        resolvedDate = dateResolved?.let { dateResolved } ?: "",
        impacts = impactsAndCausesCacheList?.filter { it.type == 1 }?.toRequest(),
        rootCause = impactsAndCausesCacheList?.filter { it.type == 0 }?.toRequest(),
        itemsBreakdown = breakdownCacheList.toRequest(),
        neededBy = needeBy ?: "",
        assignee = Assignee(assigneId, assigneeName),
        customFields = getCustomFields(pjIssueId, pjIssueIdMobile),
        neededByTimezone = neededByTimezone ?: ""
    )

    private fun getCustomFields(
        pjIssueId: Long?, pjIssueIdMobile: Long?
    ): List<IssueListItemCustomFields> {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        val valueList = if (pjIssueId != 0L) {
            issueTrackingCustomFieldsCacheDao.queryBuilder()?.where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingId.eq(
                    pjIssueId
                ), IssueTrackingCustomFieldsCacheDao.Properties.UsersId.eq(
                    loginResponse?.userDetails?.users_id ?: 0
                )
            )?.list()
        } else {
            issueTrackingCustomFieldsCacheDao.queryBuilder()?.where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingMobileId.eq(
                    pjIssueIdMobile
                ), IssueTrackingCustomFieldsCacheDao.Properties.UsersId.eq(
                    loginResponse?.userDetails?.users_id ?: 0
                )
            )?.list()
        }
        val fieldList = arrayListOf<IssueListItemCustomFields>()
        valueList?.forEach { customField ->
            fieldList.add(
                IssueListItemCustomFields(
                    "",
                    customField.issueSectionId,
                    customField.issueTrackingCustomId,
                    customField.issueTrackingCustomMobileId,
                    customField.issueTrackingId,
                    customField.issueTrackingMobileId,
                    customField.issueTrackingItemsId,
                    customField.trackingItemTypesId,
                    customField.value
                )
            )
        }
        return fieldList
    }

    @JvmName("toRequestProjectIssuesItemBreakdownCache")
    fun List<ProjectIssuesItemBreakdownCache>.toRequest() = map {
        it.toRequest()
    }

    fun ProjectIssuesItemBreakdownCache.toRequest() = IssueItemBreakdownRequest(
        pjIssuesItemBreakdownId = pjIssuesItemBreakdownId,
        description = description,
        days = days,
        amount = amount,
        pjIssuesItemBreakdownIdMobile = pjIssuesItemBreakdownIdMobile,
        deletedAt = deletedAt?.let { deletedAt } ?: "")

    fun List<ProjectIssueImpactsAndCausesCache>.toRequest() = map {
        it.toRequest()
    }

    fun ProjectIssueImpactsAndCausesCache.toRequest() = IssuesImpactAndRootCauseRequest(
        pjIssuesTrackingsId = pjIssuesTrackingsId,
        pjIssuesCauseImpactId = pjIssuesCauseImpactId,
        type = type,
        pjIssuesTrackingIdMobile = pjIssuesTrackingIdMobile,
        deletedAt = deletedAt?.let { deletedAt } ?: "")

    fun addSection(pjTrackingSectionsList: ArrayList<PjTrackingSections>, usersId: Int) {
        pjTrackingSectionsList.forEach { pjTrackingSections ->
            var sectionCache: IssueTrackingSectionCache? = null
            try {
                sectionCache = issueTrackingSectionDao.queryBuilder()?.where(
                    IssueTrackingSectionCacheDao.Properties.Issue_tracking_sections_id.eq(
                        pjTrackingSections.issueTrackingSectionsId
                    ), IssueTrackingSectionCacheDao.Properties.UsersId.eq(
                        usersId
                    )
                )?.unique()
                if (sectionCache == null) {
                    val updateDate =
                        if (pjTrackingSections.updatedAt != null && pjTrackingSections.updatedAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.updatedAt
                        )
                        else null
                    val createdDate =
                        if (pjTrackingSections.createdAt != null && pjTrackingSections.createdAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.createdAt
                        )
                        else null
                    val deletedDate =
                        if (pjTrackingSections.deletedAt != null && pjTrackingSections.deletedAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.deletedAt
                        )
                        else null
                    val issueSectionCache = IssueTrackingSectionCache()
                    issueSectionCache.issue_tracking_sections_id =
                        pjTrackingSections.issueTrackingSectionsId
                    issueSectionCache.sectionName = pjTrackingSections.sectionName
                    issueSectionCache.sortOrder = pjTrackingSections.sortOrder
                    issueSectionCache.tenantId = pjTrackingSections.tenantId
                    issueSectionCache.usersId = usersId
                    issueSectionCache.createdAt = createdDate
                    issueSectionCache.updatedAt = updateDate
                    issueSectionCache.deletedAt = deletedDate
                    if (deletedDate == null) {
                        issueTrackingSectionDao.insert(
                            issueSectionCache
                        )
                    }
                } else {
                    sectionCache.sectionName = pjTrackingSections.sectionName
                    sectionCache.sortOrder = pjTrackingSections.sortOrder
                    sectionCache.tenantId = pjTrackingSections.tenantId
                    sectionCache.usersId = usersId
                    sectionCache.createdAt =
                        if (pjTrackingSections.createdAt != null && pjTrackingSections.createdAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.createdAt
                        ) else null
                    sectionCache.updatedAt =
                        if (pjTrackingSections.updatedAt != null && pjTrackingSections.updatedAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.updatedAt
                        ) else null

                    sectionCache.deletedAt =
                        if (pjTrackingSections.deletedAt != null && pjTrackingSections.deletedAt != "") DateFormatter.getDateFromDateTimeString(
                            pjTrackingSections.deletedAt
                        )
                        else null
                    if (sectionCache.deletedAt != null) {
                        issueTrackingSectionDao.delete(sectionCache)
                    } else {
                        issueTrackingSectionDao.update(sectionCache)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun addCustomItems(pjTrackingItemList: ArrayList<PjTrackingItems>, usersId: Int) {
        pjTrackingItemList.forEach { pjTrackingItems ->
            var itemCache: IssueTrackingItemsCache? = null
            try {
                itemCache = issueTrackingItemsCacheDao.queryBuilder()?.where(
                    IssueTrackingItemsCacheDao.Properties.IssueTrackingItemsId.eq(
                        pjTrackingItems.issueTrackingItemsId
                    ), IssueTrackingItemsCacheDao.Properties.UsersId.eq(
                        usersId
                    )
                )?.unique()
                val updateDate =
                    if (pjTrackingItems.updatedAt != null && pjTrackingItems.updatedAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItems.updatedAt
                    )
                    else null
                val createdDate =
                    if (pjTrackingItems.createdAt != null && pjTrackingItems.createdAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItems.createdAt
                    )
                    else null
                val deletedDate =
                    if (pjTrackingItems.deletedAt != null && pjTrackingItems.deletedAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItems.deletedAt
                    )
                    else null


                var additionalData: String? = null
                if (pjTrackingItems.additionalData != null) {
                    additionalData = Gson().toJson(pjTrackingItems.additionalData)
                }
                var issueTrackingItemCache = IssueTrackingItemsCache()
                if (itemCache != null) {
                    issueTrackingItemCache = itemCache
                }
                issueTrackingItemCache.issueTrackingItemsId = pjTrackingItems.issueTrackingItemsId
                issueTrackingItemCache.issueTrackingSectionsId =
                    pjTrackingItems.issueTrackingSectionsId
                issueTrackingItemCache.itemName = pjTrackingItems.itemName
                issueTrackingItemCache.tenantId = pjTrackingItems.tenantId
                issueTrackingItemCache.usersId = usersId
                issueTrackingItemCache.sortOrder = pjTrackingItems.sortOrder
                issueTrackingItemCache.additionalData = additionalData
                issueTrackingItemCache.trackingItemTypesId = pjTrackingItems.trackingItemTypesId
                issueTrackingItemCache.updated_at = updateDate
                issueTrackingItemCache.created_at = createdDate
                issueTrackingItemCache.deleted_at = deletedDate

                if (itemCache == null) {
                    if (deletedDate == null)
                        issueTrackingItemsCacheDao.insert(issueTrackingItemCache)
                } else {
                    if (itemCache.deleted_at != null) {
                        issueTrackingItemsCacheDao.delete(itemCache)
                    } else {
                        issueTrackingItemsCacheDao.update(itemCache)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCustomItemTypes(trackingItemTypes: ArrayList<TrackingItemTypes>) {
        trackingItemTypes.forEach { pjTrackingItemTypes ->
            var itemTypesCache: IssueTrackingItemTypesCache? = null
            try {
                itemTypesCache = issueTrackingItemTypesCacheDao.queryBuilder()?.where(
                    IssueTrackingItemTypesCacheDao.Properties.TrackingItemTypesId.eq(
                        pjTrackingItemTypes.trackingItemTypesId
                    )
                )?.unique()
                val updatedDate =
                    if (pjTrackingItemTypes.updatedAt != null && pjTrackingItemTypes.updatedAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItemTypes.updatedAt
                    )
                    else null
                val createdDate =
                    if (pjTrackingItemTypes.createdAt != null && pjTrackingItemTypes.createdAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItemTypes.createdAt
                    )
                    else null
                val deletedDate =
                    if (pjTrackingItemTypes.deletedAt != null && pjTrackingItemTypes.deletedAt != "") DateFormatter.getDateFromDateTimeString(
                        pjTrackingItemTypes.deletedAt
                    )
                    else null

                /*
                * Long trackingItemTypesId,
                * String label,
                * String type,
                * String options,
                * Date createdAt,
                * Date updatedAt,
                * Date deletedAt
                * */
                var issueTrackingItemTypesCache = IssueTrackingItemTypesCache()
                issueTrackingItemTypesCache.trackingItemTypesId =
                    pjTrackingItemTypes.trackingItemTypesId
                issueTrackingItemTypesCache.label = pjTrackingItemTypes.label
                issueTrackingItemTypesCache.type = pjTrackingItemTypes.type
                issueTrackingItemTypesCache.options = pjTrackingItemTypes.options
                issueTrackingItemTypesCache.createdAt = createdDate
                issueTrackingItemTypesCache.updatedAt = updatedDate
                issueTrackingItemTypesCache.deletedAt = deletedDate

                if (itemTypesCache == null) {
                    issueTrackingItemTypesCacheDao.insert(issueTrackingItemTypesCache)
                } else {
                    if (issueTrackingItemTypesCache.deletedAt != null) {
                        issueTrackingItemTypesCacheDao.delete(issueTrackingItemTypesCache)
                    } else {
                        issueTrackingItemTypesCacheDao.update(issueTrackingItemTypesCache)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSections(usersId: Int): MutableList<IssueTrackingSectionCache>? {
        return issueTrackingSectionDao.queryBuilder()?.where(
            IssueTrackingSectionCacheDao.Properties.UsersId.eq(
                usersId
            ),
            IssueTrackingSectionCacheDao.Properties.DeletedAt.isNull
        )?.orderAsc(IssueTrackingSectionCacheDao.Properties.SortOrder)?.list()
    }

    fun getItems(sectionId: Int, usersId: Int?): MutableList<IssueTrackingItemsCache>? {

        return issueTrackingItemsCacheDao.queryBuilder()?.where(
            IssueTrackingItemsCacheDao.Properties.IssueTrackingSectionsId.eq(
                sectionId
            ), IssueTrackingItemsCacheDao.Properties.UsersId.eq(
                usersId ?: 0,
            ),
            IssueTrackingItemsCacheDao.Properties.Deleted_at.isNull
        )?.orderAsc(IssueTrackingItemsCacheDao.Properties.SortOrder)?.list()
    }

    fun getItemTypes(trackingItemTypesId: Int): IssueTrackingItemTypesCache? {
        return issueTrackingItemTypesCacheDao.queryBuilder()?.where(
            IssueTrackingItemTypesCacheDao.Properties.TrackingItemTypesId.eq(
                trackingItemTypesId
            )
        )?.unique()
    }

    fun getValues(
        sectionId: Int, issueTrackingItemId: Int, pjIssueId: Long, pjIssueIdMobile: Long
    ): String? {
        val loginResponse: LoginResponse? = Gson().fromJson(
            SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        if (pjIssueId == 0L) {
            val list = issueTrackingCustomFieldsCacheDao.queryBuilder()?.where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingItemsId.eq(
                    issueTrackingItemId
                ),
                IssueTrackingCustomFieldsCacheDao.Properties.IssueSectionId.eq(sectionId),
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingMobileId.eq(
                    pjIssueIdMobile
                ),
                IssueTrackingCustomFieldsCacheDao.Properties.UsersId.eq(
                    loginResponse?.userDetails?.users_id ?: 0
                ),
            )?.list()
            if (list != null && list.size > 0) {
                return list.get(list.size - 1).value
            } else {
                return ""
            }
        } else {
            val list = issueTrackingCustomFieldsCacheDao.queryBuilder()?.where(
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingItemsId.eq(
                    issueTrackingItemId
                ),
                IssueTrackingCustomFieldsCacheDao.Properties.IssueSectionId.eq(sectionId),
                IssueTrackingCustomFieldsCacheDao.Properties.IssueTrackingId.eq(pjIssueId),
                IssueTrackingCustomFieldsCacheDao.Properties.UsersId.eq(
                    loginResponse?.userDetails?.users_id ?: 0
                )
            )?.list()
            if (list != null && list.size > 0) {
                return list.get(list.size - 1).value
            } else {
                return ""
            }
        }
    }


    fun getAdditionalData(trackingItemTypesId: Int?, usersId: Int): String? {
        val list = issueTrackingItemsCacheDao.queryBuilder()?.where(
            IssueTrackingItemsCacheDao.Properties.TrackingItemTypesId.eq(trackingItemTypesId),
            IssueTrackingItemsCacheDao.Properties.UsersId.eq(usersId)
        )?.list()
        val additionalData = list?.get(list.size - 1)?.additionalData
        return additionalData
    }
}
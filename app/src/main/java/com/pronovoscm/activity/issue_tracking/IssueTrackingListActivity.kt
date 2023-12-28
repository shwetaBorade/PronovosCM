package com.pronovoscm.activity.issue_tracking

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pronovoscm.R
import com.pronovoscm.activity.LoginActivity
import com.pronovoscm.adapter.ImpactAndRootCauseAdapter
import com.pronovoscm.adapter.IssueTrackingListAdapter
import com.pronovoscm.data.ProviderResult
import com.pronovoscm.data.issuetracking.DataSource
import com.pronovoscm.data.issuetracking.IssueDataProcess.*
import com.pronovoscm.data.issuetracking.IssueListRequestParam
import com.pronovoscm.data.issuetracking.IssueListResponse
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ImpactRootCauseFilterBinding
import com.pronovoscm.databinding.IssuesFilterBinding
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.model.view.IssueListItem
import com.pronovoscm.model.view.IssueStatus
import com.pronovoscm.model.view.MasterImpactAndRootCause
import com.pronovoscm.services.NetworkService
import com.pronovoscm.utils.Constants
import com.pronovoscm.utils.SharedPref
import com.pronovoscm.utils.ui.CustomProgressBar
import kotlinx.android.synthetic.main.activity_list_issue_tracking.*
import kotlinx.android.synthetic.main.toolbar_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class IssueTrackingListActivity : BaseIssueTrackingActivity<List<IssueListItem>>() {

    var isOffline = false
    private var mProjectId = 0
    private lateinit var issueTrackingListAdapter: IssueTrackingListAdapter

    @Inject
    lateinit var projectIssueTrackingProvider: ProjectIssueTrackingProvider

    lateinit var loginResponse: LoginResponse

    override fun doGetContentView(): Int {
        return R.layout.activity_list_issue_tracking
    }

    private val onIssueCardClick: (pjIssueId: Long, pjIssueIdMobile: Long) -> Unit = { pjIssueId, pjIssueIdMobile ->
        isOffline = true
        val intent = Intent(this@IssueTrackingListActivity, ViewIssueDetailActivity::class.java)
        intent.putExtra(Constants.INTENT_KEY_PROJECT_ID, mProjectId)
        intent.putExtra(Constants.INTENT_KEY_PJ_ISSUE_ID, pjIssueId)
        intent.putExtra(Constants.INTENT_KEY_PJ_ISSUE_ID_MOBILE, pjIssueIdMobile)
        startActivity(intent)
    }

    val downloadIcon: (iconUrl: String, impactAndRootCauseId: Long, imageView: ImageView) -> Unit = { iconUrl, impactAndRootCauseId, imageView ->

        var icon: Bitmap? = null

        lifecycleScope.launch {

            icon = withContext(Dispatchers.IO) {
                Glide.with(this@IssueTrackingListActivity)
                        .asBitmap()
                        .load(iconUrl)
                        .submit()
                        .get()
            }

            icon?.let { safeIcon ->
                imageView.setImageBitmap(safeIcon)
                storeIcon(
                        icon = safeIcon,
                        fileName = iconUrl.substring(iconUrl.lastIndexOf("/") + 1),
                        impactAndRootCauseId = impactAndRootCauseId
                )
            }
        }
    }

    private val issuesDataCallback = object : ProviderResult<IssueListResponse<List<IssueListItem>>> {
        override fun success(result: IssueListResponse<List<IssueListItem>>) {
            handleSuccess(issueListResponse = result)
        }

        override fun AccessTokenFailure(message: String) {
            CustomProgressBar.dissMissDialog(this@IssueTrackingListActivity)
            noRecordTextView.isVisible = false
            handleAccessTokenFails()
        }

        override fun failure(message: String) {
            CustomProgressBar.dissMissDialog(this@IssueTrackingListActivity)
            noRecordTextView.isVisible = true
            noRecordTextView.text = message
            rvIssues.isVisible = false
        }
    }

    var searchString: String? = null
    var selectedImpactId: Long? = null
    var selectedRootCauseId: Long? = null
    var isResolved: Boolean? = null
    lateinit var statusSelectedText: String
    lateinit var rootCauseText: String
    lateinit var impactText: String

    var isActivityCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusSelectedText = getString(R.string.action_filter_thumbnails_all)
        rootCauseText = getString(R.string.select)
        impactText = getString(R.string.select)

        doGetApplication().daggerComponent.inject(this@IssueTrackingListActivity)

        loginResponse = Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse::class.java)

        mProjectId = intent.getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0)

        noRecordTextView.isVisible = false
        rvIssues.isVisible = false

        initUI()
    }

    private fun initUI() {

        titleTextView.text = getString(R.string.issue_tracking)

        with(leftImageView) {
            setOnClickListener {
                onBackPressed()
            }
            setImageResource(R.drawable.ic_arrow_back)
        }

        rightImageView.setOnClickListener {
            openFilter()
        }

        with(addImageView) {
            val userPermissions = loginResponse.userDetails.permissions[0]
            visibility = if (userPermissions.createIssueTracking == 1) {
                setOnClickListener {
                    isOffline = true
                    val intent = Intent(this@IssueTrackingListActivity, AddIssueActivity::class.java)
                    intent.putExtra(Constants.INTENT_KEY_PROJECT_ID, mProjectId)
                    startActivity(intent)
                }
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        searchEditText.addTextChangedListener {
            searchClearImageView.isVisible = it?.isNotEmpty() ?: false
            searchString = it?.toString()
            getIssues(
                    source = DataSource.CACHE
            )
        }

        searchClearImageView.setOnClickListener {
            searchEditText.setText("")
            searchString = null
            getIssues(
                    source = DataSource.CACHE
            )
        }

        setIssueTrackingAdapter()
    }

    private fun setIssueTrackingAdapter() {
        issueTrackingListAdapter = IssueTrackingListAdapter(mutableListOf(), downloadIcon, onIssueCardClick)
        rvIssues.adapter = issueTrackingListAdapter
    }

    private fun handleSuccess(issueListResponse: IssueListResponse<List<IssueListItem>>) {

        when (issueListResponse.issueDataProcess) {
            LIST -> {
                val isNotEmpty = issueListResponse.data.isNotEmpty()
                if (isNotEmpty) {
                    issueTrackingListAdapter.setIssuesList(issueListResponse.data.toMutableList())
                }
                noRecordTextView.isVisible = isNotEmpty.not()
                noRecordTextView.text = getString(R.string.no_issues_available)
                rvIssues.isVisible = isNotEmpty
            }
            ADD -> {
                val isNotEmpty = issueListResponse.data.isNotEmpty()
                if (isNotEmpty) {
                    val index = issueTrackingListAdapter.addIssue(issueListResponse.data.first())
                    if (index != -1) {
                        rvIssues?.layoutManager?.scrollToPosition(index)
                    }
                }
            }
            UPDATE -> {
                val isNotEmpty = issueListResponse.data.isNotEmpty()
                if (isNotEmpty) {
                    issueTrackingListAdapter.updateIssue(issueListResponse.data.first())
                }
            }
            FORCE_REFRESH -> getIssues(
                    source = DataSource.CACHE
            )
        }

        CustomProgressBar.dissMissDialog(this@IssueTrackingListActivity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == Constants.FILESTORAGE_REQUEST_CODE) {
            getIssues(
                    DataSource.CACHE
            )
        }
    }

    private fun handleAccessTokenFails() {
        startActivity(Intent(this@IssueTrackingListActivity, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK))
        SharedPref.getInstance(this@IssueTrackingListActivity).writePrefs(SharedPref.SESSION_DETAILS, null)
        SharedPref.getInstance(this@IssueTrackingListActivity).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0")
        finish()
    }

    private fun getIssues(source: DataSource) {

        val isNetworkCall = projectIssueTrackingProvider.getIssueList(
                IssueListRequestParam(
                        projectId = mProjectId.toLong(),
                        searchQuery = searchString,
                        status = isResolved,
                        rootCauseAndImpactIds = listOfNotNull(selectedImpactId, selectedRootCauseId),
                        source = source
                ),
                issuesDataCallback
        )

        if (isNetworkCall)
            CustomProgressBar.showDialog(this@IssueTrackingListActivity)
    }

    override fun onResume() {
        super.onResume()
        offlineTextView.isGone = NetworkService.isNetworkAvailable(this@IssueTrackingListActivity)
        if(isActivityCreated){
            getIssues(
                    source = DataSource.CACHE
            )
        } else {
            isActivityCreated = true
            getIssues(
                    source = DataSource.FORCE_REFRESH_NETWORK
            )
        }
    }

    private fun openFilter() {

        val filterDialog = AlertDialog.Builder(this@IssueTrackingListActivity).create()

        val issuesFilterBinding = IssuesFilterBinding.inflate(layoutInflater, null, false).apply {

            tvStatus.text = statusSelectedText
            tvImpacts.text = impactText
            tvRootCause.text = rootCauseText

            rlStatus.setOnClickListener {
                openSubFilter(
                        filterType = FilterType.STATUS,
                        isResolved?.let {
                            if (it) 2L else 1L
                        } ?: 0
                ) { text, id ->
                    isResolved = if (id == 1L) false else if (id == 2L) true else null
                    tvStatus.text = text
                    statusSelectedText = text
                }
            }

            rlRootCause.setOnClickListener {
                openSubFilter(
                        filterType = FilterType.ROOT_CAUSE,
                        selectedRootCauseId ?: -1
                ) { text, id ->
                    selectedRootCauseId = id
                    tvRootCause.text = text
                    rootCauseText = text
                }
            }

            rlImpact.setOnClickListener {
                openSubFilter(
                        filterType = FilterType.IMPACT,
                        selectedImpactId ?: -1
                ) { text, id ->
                    selectedImpactId = id
                    tvImpacts.text = text
                    impactText = text
                }
            }
        }

        filterDialog.setView(issuesFilterBinding.root)

        filterDialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                getString(R.string.save)
        ) { dialog, _ ->
            searchString = null
            searchEditText.text = null

            getIssues(
                    source = DataSource.CACHE
            )

            if (isResolved != null && selectedRootCauseId != null && selectedImpactId != null) {
                filterTextView.text = "3"
                filterTextView.isVisible = true
            } else if ((isResolved != null && selectedRootCauseId != null) || (isResolved != null && selectedImpactId != null) || (selectedRootCauseId != null && selectedImpactId != null)) {
                filterTextView.text = "2"
                filterTextView.isVisible = true
                //rightImageView.background = ContextCompat.getDrawable(this, R.drawable.ic_filter_regions)
            } else if (isResolved != null || selectedRootCauseId != null || selectedImpactId != null) {
                filterTextView.text = "1"
                filterTextView.isVisible = true
            } else {
                filterTextView.text = null
                filterTextView.isVisible = false
            }
        }

        filterDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        filterDialog.show()
        filterDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.gray_948d8d))
    }

    private fun openSubFilter(
            filterType: FilterType,
            selectedId: Long,
            onSelect: (String, Long?) -> Unit
    ) {

        val filterDialog = AlertDialog.Builder(this@IssueTrackingListActivity).create()

        val binding = ImpactRootCauseFilterBinding.inflate(layoutInflater, null, false)

        val adapter = when (filterType) {
            FilterType.STATUS -> {
                binding.tvLabelFilter.text = "Select status"
                ImpactAndRootCauseAdapter(
                        impactAndRootCauseList = listOf(
                                IssueStatus(
                                        id = 0,
                                        name = getString(R.string.action_filter_thumbnails_all),
                                        isResolved = null,
                                        isSelected = selectedId == 0L
                                ),
                                IssueStatus(
                                        id = 1,
                                        name = "Open",
                                        isResolved = false
                                ),
                                IssueStatus(
                                        id = 2,
                                        name = "Resolved",
                                        isResolved = true
                                )
                        ).apply {
                            firstOrNull { it.id == selectedId.toInt() }?.isSelected = true
                        },
                        viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_ISSUE_STATUS,
                        useMultiSelection = false,
                        downloadIcon = downloadIcon
                )
            }
            FilterType.ROOT_CAUSE -> {
                binding.tvLabelFilter.text = "Select a root cause"
                ImpactAndRootCauseAdapter(
                        impactAndRootCauseList = projectIssueTrackingProvider.getImpactsAndRootCause().filter { it.impactStatus == 0 }.apply {
                            firstOrNull { it.pjIssueCauseImpactId == selectedId }?.isSelected = true
                        },
                        viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_FILTER,
                        useMultiSelection = false,
                        downloadIcon = downloadIcon
                )
            }
            FilterType.IMPACT -> {
                binding.tvLabelFilter.text = "Select impact"
                ImpactAndRootCauseAdapter(
                        impactAndRootCauseList = projectIssueTrackingProvider.getImpactsAndRootCause().filter { it.impactStatus == 1 }.apply {
                            firstOrNull { it.pjIssueCauseImpactId == selectedId }?.isSelected = true
                        },
                        viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_FILTER,
                        useMultiSelection = false,
                        downloadIcon = downloadIcon
                )
            }
        }

        binding.rvImpactsRootCause.adapter = adapter

        filterDialog.setView(binding.root)

        filterDialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                getString(R.string.save)
        ) { dialog, _ ->
            when (filterType) {
                FilterType.STATUS -> {
                    adapter.getList().firstOrNull { (it as IssueStatus).isSelected }?.let {
                        val selected = (it as IssueStatus)
                        onSelect(selected.name, selected.id.toLong())
                    } ?: onSelect(getString(R.string.action_filter_thumbnails_all), 0)
                }
                FilterType.ROOT_CAUSE -> {
                    adapter.getList().firstOrNull { (it as MasterImpactAndRootCause).isSelected }?.let {
                        val selected = (it as MasterImpactAndRootCause)
                        onSelect(selected.name, selected.pjIssueCauseImpactId)
                    } ?: onSelect(getString(R.string.select), null)
                }
                FilterType.IMPACT -> {
                    adapter.getList().firstOrNull { (it as MasterImpactAndRootCause).isSelected }?.let {
                        val selected = (it as MasterImpactAndRootCause)
                        onSelect(selected.name, selected.pjIssueCauseImpactId)
                    } ?: onSelect(getString(R.string.select), null)
                }
            }
        }

        filterDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        filterDialog.show()
        filterDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.gray_948d8d))
    }

    suspend fun storeIcon(icon: Bitmap, fileName: String, impactAndRootCauseId: Long): String {

        val iconFolder = File(this@IssueTrackingListActivity.filesDir, "Pronovos/ImpactsAndRootCause")
        if (!iconFolder.exists()) {
            iconFolder.mkdir()
        }
        val iconFile = File(iconFolder, fileName)
        if (iconFile.exists()) {
            iconFile.delete()
        }
        val out = withContext(Dispatchers.IO) {
            FileOutputStream(iconFile)
        }
        icon.compress(Bitmap.CompressFormat.PNG, 85, out)
        withContext(Dispatchers.IO) {
            out.flush()
            out.close()
        }

        projectIssueTrackingProvider.storeImpactAndRootCauseFilePath(iconFile.absolutePath, impactAndRootCauseId)

        return iconFile.absolutePath
    }

    override fun toggleOfflineView(event: Boolean?) {
        event?.let {
            isOffline = event

            offlineTextView.isVisible = event

            if (isOffline.not()) {
                getIssues(
                        source = DataSource.FORCE_REFRESH_NETWORK
                )
            }
        }
    }

    override fun dataUpdate(issueListResponse: IssueListResponse<List<IssueListItem>>?) {
        issueListResponse?.let { handleSuccess(it) }
    }

    enum class FilterType {
        STATUS, ROOT_CAUSE, IMPACT
    }
}
package com.pronovoscm.activity.issue_tracking

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pronovoscm.R
import com.pronovoscm.adapter.CustomFieldAdapter
import com.pronovoscm.adapter.ImpactAndRootCauseAdapter
import com.pronovoscm.adapter.IssueBreakdownAdapter
import com.pronovoscm.data.ProviderResult
import com.pronovoscm.data.issuetracking.IssueListResponse
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ActivityViewIssueDetailBinding
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.model.view.CustomFieldsValues
import com.pronovoscm.model.view.ImpactAndRootCause
import com.pronovoscm.model.view.IssueBreakdown
import com.pronovoscm.model.view.IssueListItem
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

class ViewIssueDetailActivity : BaseIssueTrackingActivity<IssueListItem>() {

    private lateinit var binding: ActivityViewIssueDetailBinding

    var isOffline = false
    private var projectId: Int = 0
    private var pjIssueId: Long = 0L
    private var pjIssueIdMobile: Long = 0L

    @Inject
    lateinit var projectIssueTrackingProvider: ProjectIssueTrackingProvider

    lateinit var loginResponse: LoginResponse

    val downloadIcon: (iconUrl: String, impactAndRootCauseId: Long, imageView: ImageView) -> Unit = { iconUrl, impactAndRootCauseId, imageView ->

        var icon: Bitmap? = null

        lifecycleScope.launch {

            icon = withContext(Dispatchers.IO) {
                Glide.with(this@ViewIssueDetailActivity)
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

    private val editActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun doGetContentView(): Int {
        return R.layout.activity_view_issue_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doGetApplication().daggerComponent.inject(this@ViewIssueDetailActivity)

        loginResponse = Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse::class.java)

        intent?.extras?.let { safeExtras ->
            projectId = safeExtras.getInt(Constants.INTENT_KEY_PROJECT_ID, 0)
            pjIssueId = safeExtras.getLong(Constants.INTENT_KEY_PJ_ISSUE_ID)
            pjIssueIdMobile = safeExtras.getLong(Constants.INTENT_KEY_PJ_ISSUE_ID_MOBILE)
        }

        getIssueDetails(true)
    }

    private fun initUI(issueListItem: IssueListItem) {

        binding = DataBindingUtil.setContentView<ActivityViewIssueDetailBinding?>(this@ViewIssueDetailActivity, R.layout.activity_view_issue_detail).apply {
            lifecycleOwner = this@ViewIssueDetailActivity
            issueTracking = issueListItem
        }

        issueListItem.issuesBreakdown.setAdapter()
        issueListItem.impactsAndRootCause.filter { it.type == 1 }.setImpactsAdapter()
        issueListItem.impactsAndRootCause.filter { it.type == 0 }.setRootCauseAdapter()

        titleTextView.text = "Issue #${issueListItem.issueNumber}"

        with(leftImageView) {
            setOnClickListener {
                onBackPressed()
            }
            setImageResource(R.drawable.ic_arrow_back)
        }
        with(editImageView) {

            val userPermissions = loginResponse.userDetails.permissions[0]
            visibility = if (userPermissions.editIssueTracking == 1) {
                setOnClickListener {
                    doEdit()
                }
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        rightImageView?.isVisible = false

        binding.btnDelete.setOnClickListener {
            showDeleteConfirm(issueListItem.pjIssueId, issueListItem.pjIssueIdMobile)
        }
    }

    private fun showDeleteConfirm(pjIssueId: Long, pjIssueIdMobile: Long) {

        val dialog = AlertDialog.Builder(this@ViewIssueDetailActivity).create()

        dialog.setTitle(R.string.confirm)
        dialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_issue))
        dialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.cancel),
        ) { d, i ->
            d.dismiss()
        }

        dialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                getString(R.string.yes)
        ) { d, i ->
            projectIssueTrackingProvider.deleteIssue(pjIssueId, pjIssueIdMobile)
            setResult(Activity.RESULT_OK)
            finish()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.gray_948d8d))
    }

    private fun getIssueDetails(showProgress: Boolean) {
        if (showProgress) CustomProgressBar.showDialog(this@ViewIssueDetailActivity)

        projectIssueTrackingProvider.getIssueDetails(
                pjIssueId = pjIssueId,
                pjIssuesIdMobile = pjIssueIdMobile,
                issueListItemResult = object : ProviderResult<IssueListItem> {
                    override fun success(result: IssueListItem?) {
                        CustomProgressBar.dissMissDialog(this@ViewIssueDetailActivity)
                        result?.let { safeResult ->
                            safeResult.apply {
                                issuesBreakdown = issuesBreakdown.filter { it.deletedAt.isNullOrEmpty() }
                                impactsAndRootCause = impactsAndRootCause.filter { it.deletedAt.isNullOrEmpty() }
                            }
                            initUI(safeResult)
                        }
                    }

                    override fun AccessTokenFailure(message: String?) {

                    }

                    override fun failure(message: String?) {
                        //TODO Check Error
                        CustomProgressBar.dissMissDialog(this@ViewIssueDetailActivity)
                    }
                })
        setCustomFieldAdapter()
    }

    private fun setCustomFieldAdapter() {
        val setData = { _:CustomFieldsValues-> }
        val  customFieldAdapter = CustomFieldAdapter(projectIssueTrackingProvider.getSections(),projectIssueTrackingProvider,null,pjIssueId,pjIssueIdMobile,setData)
        binding.rvCustomFields.adapter = customFieldAdapter
    }
    private fun List<IssueBreakdown>.setAdapter() {
        val adapter = IssueBreakdownAdapter(this.toMutableList())
        binding.rvIssueBreakdown.adapter = adapter
    }

    @JvmName("setAdapterImpactAndRootCause")
    private fun List<ImpactAndRootCause>.setImpactsAdapter() {
        val adapter = ImpactAndRootCauseAdapter(
                impactAndRootCauseList = this,
                viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_VIEW_ISSUE,
                useMultiSelection = false,
                downloadIcon = downloadIcon
        )
        binding.rvImpacts.adapter = adapter
    }

    private fun List<ImpactAndRootCause>.setRootCauseAdapter() {
        val adapter = ImpactAndRootCauseAdapter(
                impactAndRootCauseList = this,
                viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_VIEW_ISSUE,
                useMultiSelection = false,
                downloadIcon = downloadIcon
        )
        binding.rvRootCause.adapter = adapter
    }

    fun doEdit() {
        val intent = Intent(this@ViewIssueDetailActivity, AddIssueActivity::class.java)
        intent.putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId)
        intent.putExtra(Constants.INTENT_KEY_PJ_ISSUE_ID, pjIssueId)
        intent.putExtra(Constants.INTENT_KEY_PJ_ISSUE_ID_MOBILE, pjIssueIdMobile)
        editActivityLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        offlineTextView.isGone = NetworkService.isNetworkAvailable(this@ViewIssueDetailActivity)
    }

    override fun toggleOfflineView(event: Boolean?) {
        event?.let {
            isOffline = event

            offlineTextView.isVisible = event
        }
    }

    override fun dataUpdate(issueListResponse: IssueListResponse<IssueListItem>?) {

    }

    suspend fun storeIcon(icon: Bitmap, fileName: String, impactAndRootCauseId: Long): String {

        val iconFolder = File(this@ViewIssueDetailActivity.filesDir, "Pronovos/ImpactsAndRootCause")
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
}
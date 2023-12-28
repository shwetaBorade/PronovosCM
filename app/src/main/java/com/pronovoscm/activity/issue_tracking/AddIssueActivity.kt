package com.pronovoscm.activity.issue_tracking

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pronovoscm.R
import com.pronovoscm.adapter.CustomFieldAdapter
import com.pronovoscm.adapter.ImpactAndRootCauseAdapter
import com.pronovoscm.adapter.IssueBreakdownAdapter
import com.pronovoscm.data.FieldPaperWorkProvider
import com.pronovoscm.data.ProviderResult
import com.pronovoscm.data.issuetracking.IssueListResponse
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ActivityAddIssueBinding
import com.pronovoscm.databinding.ImpactRootCauseFilterBinding
import com.pronovoscm.materialchips.ChipsInput
import com.pronovoscm.materialchips.ChipsInput.ChipsListener
import com.pronovoscm.materialchips.model.ChipInterface
import com.pronovoscm.model.request.assignee.AssigneeRequest
import com.pronovoscm.model.response.issueTracking.issues.Assignee
import com.pronovoscm.model.response.issueTracking.issues.TimeZone
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.model.view.*
import com.pronovoscm.persistence.domain.PunchlistAssignee
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingSectionCache
import com.pronovoscm.services.NetworkService
import com.pronovoscm.ui.punchlist.adapter.PunchlistAssigneeList
import com.pronovoscm.utils.Constants
import com.pronovoscm.utils.DateFormatter
import com.pronovoscm.utils.SharedPref
import com.pronovoscm.utils.dialogs.CustomFieldTimezoneDialog
import com.pronovoscm.utils.ui.CustomProgressBar
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_add_issue.*
import kotlinx.android.synthetic.main.toolbar_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat

class AddIssueActivity : BaseIssueTrackingActivity<IssueListItem>() {


    private var projectId: Int = 0
    private var pjIssueId: Long = 0L
    private var pjIssueIdMobile: Long = 0L
    private var isEdit: Boolean = false
    private var previousSelectedBox = TimeZone()
    private lateinit var binding: ActivityAddIssueBinding

    private lateinit var impactsAdapter: ImpactAndRootCauseAdapter
    private lateinit var rootCauseAdapter: ImpactAndRootCauseAdapter
    private lateinit var issueBreakdownAdapter: IssueBreakdownAdapter
    private lateinit var customFieldAdapter: CustomFieldAdapter
    var isOffline = false

    @Inject
    lateinit var projectIssueTrackingProvider: ProjectIssueTrackingProvider

    lateinit var loginResponse: LoginResponse

    lateinit var previousIssueItem: IssueListItem


    private val previousImpactAndRootCause: MutableList<IssueImpactAndRootCause> = mutableListOf()
    private val previousBreakdown: MutableList<IssueBreakdown> = mutableListOf()


    val downloadIcon: (iconUrl: String, impactAndRootCauseId: Long, imageView: ImageView) -> Unit =
        { iconUrl, impactAndRootCauseId, imageView ->
            var icon: Bitmap? = null

            lifecycleScope.launch {

                if (NetworkService.isNetworkAvailable(this@AddIssueActivity)) {
                    icon = withContext(Dispatchers.IO) {
                        Glide.with(this@AddIssueActivity)
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
        }

    val toggleRootCauseError: (Int) -> Unit = { selectedId ->
        binding.tvLabelRootCauseError.isVisible = selectedId == -1
    }

    val deleteBreakdown: (Int) -> Unit = { index ->
        // showBreakdownDeleteConfirm(index)
        issueBreakdownAdapter.deleteBreakDown(index) {
            binding.tvLabelItemBreakdownCount.text =
                issueBreakdownAdapter.getActualCount().toString()
        }
    }

    @Inject
    lateinit var mFieldPaperWorkProvider: FieldPaperWorkProvider
    private lateinit var mPunchListAssignees: MutableList<PunchlistAssignee>
    var filterToList: MutableList<PunchlistAssigneeList> = ArrayList()
    private var punchlistAssigneeLists: MutableList<PunchlistAssigneeList> = ArrayList()
    private var mFilteredSelectedAssignedLists: MutableList<PunchlistAssigneeList?> = ArrayList()
    private lateinit var assignedTo: PunchlistAssigneeList
    private var sectionList: List<IssueTrackingSectionCache>? = null
    private var customFieldHash: HashMap<String, CustomFields> = hashMapOf()
    private var customFields: CustomFieldsValues = CustomFieldsValues()
    override fun doGetContentView(): Int = R.layout.activity_add_issue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doGetApplication().daggerComponent.inject(this@AddIssueActivity)
        loginResponse = Gson().fromJson(
            SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )
        intent.extras?.let { safeExtras ->
            projectId = safeExtras.getInt(Constants.INTENT_KEY_PROJECT_ID, -1)
            pjIssueId = safeExtras.getLong(Constants.INTENT_KEY_PJ_ISSUE_ID, -1)
            pjIssueIdMobile = safeExtras.getLong(Constants.INTENT_KEY_PJ_ISSUE_ID_MOBILE, -1)
            isEdit = pjIssueId != -1L
        }

        if (isEdit) {
            btnSave.text = getString(R.string.update)
            getIssueDetails(showProgress = true)
        } else {
            btnSave.text = getString(R.string.save)
            initUI(
                issueListItem = IssueListItem(
                    pjProjectsId = projectId.toLong(),
                    pjIssueIdMobile = projectIssueTrackingProvider.generateMobileId(
                        ProjectIssueTrackingProvider.MobileIdType.ISSUE
                    ),
                    usersId = projectIssueTrackingProvider.getUserId(),
                    tenantId = projectIssueTrackingProvider.getTenantId(),
                    dateCreated = DateFormatter.formatDateTimeForService(Date()),
                    createdBy = projectIssueTrackingProvider.getUserId(),
                    createdByName = projectIssueTrackingProvider.getCreatedByName(),
                    cacheId = null,
                    neededBy = null,
                    assignee = null,
                    neededByTimeZone = null
                )
            )
        }
    }

    private fun initUI(issueListItem: IssueListItem) {

        previousIssueItem = issueListItem.copy()
        previousSelectedBox = TimeZone(
            issueListItem.neededByTimeZone.toString(),
            issueListItem.neededByTimeZone.toString()
        )
        pjIssueId = issueListItem.pjIssueId
        pjIssueIdMobile = issueListItem.pjIssueIdMobile

        binding = DataBindingUtil.setContentView<ActivityAddIssueBinding>(
            this@AddIssueActivity,
            R.layout.activity_add_issue
        ).apply {
            lifecycleOwner = this@AddIssueActivity
            issueTracking = issueListItem

            tvLabelItemBreakdownCount.text = issueListItem.issuesBreakdown.size.toString()

            ivAddBreakdown.setOnClickListener {

                issueBreakdownAdapter.addItem(
                    IssueBreakdown()
                ) {
                    binding.tvLabelItemBreakdownCount.text =
                        issueBreakdownAdapter.getActualCount().toString()
                }
            }

            /*statusSpinner.setSelection(if (issueListItem.resolvedStatus) 1 else 0)

            statusSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val text: String = parent?.getItemAtPosition(position).toString()
                    issueListItem.resolvedStatus = position == 1
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }*/

            btnSave.text = if (isEdit) {
                deleteImageView.isVisible = true
                deleteImageView.setOnClickListener {
                    showDeleteConfirm(issueListItem.pjIssueId, issueListItem.pjIssueIdMobile)
                }
                getString(R.string.update)
            } else {
                getString(R.string.save)
            }

            btnSave.setOnClickListener {
                if (isEdit)
                    editIssue()
                else
                    saveIssue()
            }

            addAssigneeChipsInput.setChipDeletable(true)
            addAssigneeChipsInput.isShowChipDetailed = false
            setData(addAssigneeChipsInput)
            tvAddNeededBy.setOnClickListener {
                onDateClick()
            }
            tvAddNeededByTimezone.setOnClickListener() {
                val setData =
                    { selectedOption: TimeZone?, selectedMultipleOptions: ArrayList<TimeZone>? ->
                        if (selectedOption != null) {
                            tvAddNeededByTimezone.text = selectedOption.name
                            previousSelectedBox = selectedOption
                        }
                    }
                openTimezoneDialog(setData, previousSelectedBox)
            }
            tvAddNeededByTime.setOnClickListener {
                val timePicker: TimePickerDialog = TimePickerDialog(
                    // pass the Context
                    this@AddIssueActivity,
                    // listener to perform task
                    // when time is picked
                    timePickerDialogListener,
                    // default hour when the time picker
                    // dialog is opened
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    // default minute when the time picker
                    // dialog is opened
                    Calendar.getInstance().get(Calendar.MINUTE),
                    // 24 hours time picker is
                    // false (varies according to the region)
                    true
                )
                // then after building the timepicker
                // dialog show the dialog to user
                timePicker.show()
            }
        }
        sectionList = projectIssueTrackingProvider.getSections()
        callAssigneeAPI(projectId, issueListItem)

        issueListItem.impactsAndRootCause.setImpactsAndRootCauseAdapter()
        issueListItem.issuesBreakdown.setItemBreakDownAdapter()
        setCustomFieldAdapter()
        binding.ivAddBreakdown.performClick()

        loginResponse = Gson().fromJson(
            SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS),
            LoginResponse::class.java
        )

        titleTextView.text = "Issue #${issueListItem.issueNumber}"

        with(leftImageView) {
            setOnClickListener {
                onBackPressed()
            }
            setImageResource(R.drawable.ic_arrow_back)
        }
        rightImageView?.isVisible = false

        //set Text Listeners
        etIssueTitle.addTextChangedListener { text ->
            if (text?.isNotEmpty() == true) {
                tilIssueTitle.error = null
            } else {
                tilIssueTitle.error = getString(R.string.please_enter_title)
            }
        }
        binding.etIssueStatus.setOnClickListener {
            openStatusSelection()
        }
    }

    private fun openTimezoneDialog(
        setData: (TimeZone?, ArrayList<TimeZone>?) -> Unit,
        previousSelectedBox: TimeZone
    ) {
        val fm: FragmentManager = supportFragmentManager
        val ft = fm.beginTransaction()
        val tagsDialog =
            CustomFieldTimezoneDialog(
                InputTypes.radio.toString(),
                this@AddIssueActivity,
                setData,
                previousSelectedBox
            )
        val bundle = Bundle()

        // bundle.putParcelableArrayList("listOfOptions", timezone)
        tagsDialog.isCancelable = true
        tagsDialog.arguments = bundle
        tagsDialog.show(ft, "")
    }

    // listener which is triggered when the
    // time is picked from the time picker dialog
    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                // logic to properly handle
                // the picked timings by user
                val formattedTime: String = when {
                    hourOfDay == 0 -> {
                        if (minute < 10) {
                            "${hourOfDay + 12}:0${minute} am"
                        } else {
                            "${hourOfDay + 12}:${minute} am"
                        }
                    }
//
                    hourOfDay > 12 -> {
                        if (minute < 10) {
//                            hourOfDay.toString().plus(":0").plus(minute)
                            "${hourOfDay - 12}:0${minute} pm"
                        } else {
//                            hourOfDay.toString().plus(":").plus(minute)
                            "${hourOfDay - 12}:${minute} pm"
                        }
                    }
//
                    hourOfDay == 12 -> {
                        if (minute < 10) {
                            "${hourOfDay}:0${minute} pm"
                        } else {
                            "${hourOfDay}:${minute} pm"
                        }
                    }
//
                    else -> {
                        if (minute < 10) {
                            "${hourOfDay}:0${minute} am"
                        } else {
                            "${hourOfDay}:${minute} am"
                        }
                    }
                }

                tvAddNeededByTime.text = formattedTime
            }
        }


    private fun callAssigneeAPI(projectId: Int, issueListItem: IssueListItem) {
        val assigneeRequest = AssigneeRequest()
        assigneeRequest.projectId = projectId
        mFieldPaperWorkProvider.getAssignee(assigneeRequest, object :
            ProviderResult<List<PunchlistAssignee>> {
            override fun success(result: List<PunchlistAssignee>?) {
                mPunchListAssignees = mutableListOf()
                if (result != null) {
                    mPunchListAssignees.addAll(result)
                    if (isEdit) {
                        getAssigneeOnEdit(issueListItem.assignee, mPunchListAssignees)
                    }
                    addAssignee(mPunchListAssignees)
                }

            }

            override fun AccessTokenFailure(message: String?) {
                //Not impl
            }

            override fun failure(message: String?) {
                //not impl
            }

        })
    }

    private fun getAssigneeOnEdit(
        assignee: Assignee?,
        mPunchListAssignees: MutableList<PunchlistAssignee>
    ) {
        val selectedAssignee = PunchlistAssigneeList()
        if (assignee != null) {
            mPunchListAssignees.forEach { obj ->
                if (obj.usersId == assignee.assigneeId.toInt()) {
                    selectedAssignee.userId = obj.userId
                    selectedAssignee.name = obj.name
                    selectedAssignee.usersId = obj.usersId
                    selectedAssignee.pjProjectsId = obj.pjProjectsId
                    selectedAssignee.active = obj.active
                    selectedAssignee.defaultAssignee = obj.defaultAssignee
                    selectedAssignee.defaultCC = obj.defaultCC
                    binding.addAssigneeChipsInput.addChip(
                        selectedAssignee
                    )
                    setChip()
                    if (binding.addAssigneeChipsInput.selectedChipList.size == 1) {
                        binding.addAssigneeChipsInput.enableEditText(false)
                    }
                }
            }
        }
    }

    private fun addAssignee(mSelectedCclists: List<PunchlistAssignee>) {

        for (i in mSelectedCclists.indices) {
            val assigneeList = PunchlistAssigneeList()
            assigneeList.userId = mSelectedCclists[i].userId
            assigneeList.name = mSelectedCclists[i].name
            assigneeList.usersId = mSelectedCclists[i].usersId
            assigneeList.pjProjectsId = mSelectedCclists[i].pjProjectsId
            assigneeList.active = mSelectedCclists[i].active
            assigneeList.defaultAssignee = mSelectedCclists[i].defaultAssignee
            assigneeList.defaultCC = mSelectedCclists[i].defaultCC
            punchlistAssigneeLists.add(assigneeList)
        }

        filterToList = punchlistAssigneeLists
        addAssigneeTo(punchlistAssigneeLists)
    }

    private fun addAssigneeTo(addAssignedToList: List<PunchlistAssigneeList>) {
        punchlistAssigneeLists = addAssignedToList.toMutableList()
        setChip()
    }

    fun setChip() {
        binding.addAssigneeChipsInput.addChipsListener(object : ChipsListener {
            override fun onChipAdded(chip: ChipInterface, newSize: Int) {
                if (binding.addAssigneeChipsInput.selectedChipList.size == 1 || newSize == 1) {
                    binding.addAssigneeChipsInput.enableEditText(false)
                }

                assignedTo = chip.id as PunchlistAssigneeList
            }

            override fun onChipRemoved(chip: ChipInterface, newSize: Int) {
                binding.addAssigneeChipsInput.enableEditText(true)
                punchlistAssigneeLists.remove(chip.id as PunchlistAssigneeList)
                binding.addAssigneeChipsInput.requestEditFocus()
                mFilteredSelectedAssignedLists.remove(chip.id as PunchlistAssigneeList)

                val assigneeList = chip.id as PunchlistAssigneeList
                if (!assigneeList.active) filterToList.removeIf { item: PunchlistAssigneeList -> item.usersId === assigneeList.usersId }

            }

            override fun onTextChanged(text: CharSequence) {
            }
        })
        binding.addAssigneeChipsInput.filterableList = punchlistAssigneeLists
    }

    fun onDateClick() {
        val calendar1: Calendar = GregorianCalendar()
        val mYear = calendar1[Calendar.YEAR]
        val mMonth = calendar1[Calendar.MONTH]
        val mDay = calendar1[Calendar.DATE]
        val datePickerDialog = DatePickerDialog(
            this@AddIssueActivity,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar: Calendar = GregorianCalendar(
                    year,
                    monthOfYear,
                    dayOfMonth
                )
                var dueDate = calendar.time
                binding.issueTracking?.neededBy =
                    DateFormatter.formatDateForService(dueDate).toString()
                        .plus(binding.tvAddNeededByTime.text)
//                binding.issueTracking?.neededBy =
//                    DateFormatter.formatDateTimeForService(dueDate).toString()
                binding.tvAddNeededBy.text =
                    DateFormatter.formatDateForSubmittals(dueDate).toString()
//                binding.tvAddNeededBy.text =
//                    DateFormatter.formatDateForPunchList(dueDate).toString()
            }, mYear, mMonth, mDay
        )
        val calendar: Calendar = GregorianCalendar()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.show()
    }

    private fun setData(addAssigneeChipsInput: ChipsInput) {
        addAssigneeChipsInput.setChipDeletable(true)
        addAssigneeChipsInput.setChipHasAvatarIcon(false)
        addAssigneeChipsInput.isShowChipDetailed = false
    }

    private fun showDeleteConfirm(pjIssueId: Long, pjIssueIdMobile: Long) {

        val dialog = AlertDialog.Builder(this@AddIssueActivity).create()

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
        if (showProgress) CustomProgressBar.showDialog(this@AddIssueActivity)

        projectIssueTrackingProvider.getIssueDetails(
            pjIssueId = pjIssueId,
            pjIssuesIdMobile = pjIssueIdMobile,
            issueListItemResult = object : ProviderResult<IssueListItem> {
                override fun success(result: IssueListItem?) {
                    CustomProgressBar.dissMissDialog(this@AddIssueActivity)
                    result?.let { safeResult ->
                        safeResult.apply {
                            previousImpactAndRootCause.clear()
                            previousImpactAndRootCause.addAll(impactsAndRootCause)
                            previousBreakdown.clear()
                            previousBreakdown.addAll(issuesBreakdown)

                            issuesBreakdown =
                                issuesBreakdown.filter { it.deletedAt.isNullOrEmpty() }
                            impactsAndRootCause =
                                impactsAndRootCause.filter { it.deletedAt.isNullOrEmpty() }
                        }
                        initUI(safeResult)
                    }
                }

                override fun AccessTokenFailure(message: String?) {

                }

                override fun failure(message: String?) {
                    CustomProgressBar.dissMissDialog(this@AddIssueActivity)
                }
            })
    }

    private fun List<IssueImpactAndRootCause>.setImpactsAndRootCauseAdapter() {
        val impactAndRootCause = projectIssueTrackingProvider.getImpactsAndRootCause()
        impactAndRootCause.forEach { master ->
            master.isSelected =
                count { it.pjIssuesCauseImpactId == master.pjIssueCauseImpactId } > 0
        }
        impactAndRootCause.filter { it.impactStatus == 0 }.setRootCauseAdapter()
        impactAndRootCause.filter { it.impactStatus == 1 }.setImpactsAdapter()
    }

    @JvmName("setAdapterImpactAndRootCause")
    private fun List<ImpactAndRootCause>.setImpactsAdapter() {
        impactsAdapter = ImpactAndRootCauseAdapter(
            impactAndRootCauseList = this,
            viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_MASTER,
            useMultiSelection = true,
            downloadIcon = downloadIcon,
            toggleRootCauseError = toggleRootCauseError
        )
        binding.rvImpacts.adapter = impactsAdapter
    }

    private fun List<ImpactAndRootCause>.setRootCauseAdapter() {
        rootCauseAdapter = ImpactAndRootCauseAdapter(
            impactAndRootCauseList = this,
            viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_MASTER,
            useMultiSelection = false,
            downloadIcon = downloadIcon,
            toggleRootCauseError = toggleRootCauseError
        )
        binding.rvRootCause.adapter = rootCauseAdapter
    }

    private fun List<IssueBreakdown>.setItemBreakDownAdapter() {
        issueBreakdownAdapter = IssueBreakdownAdapter(
            this.toMutableList(),
            IssueBreakdownAdapter.TYPE_ADD,
            deleteBreakdown
        )
        binding.rvLabelItemBreakdown.adapter = issueBreakdownAdapter
    }

    private fun setCustomFieldAdapter() {
        val setCustomFieldData = { data: CustomFieldsValues ->
            data.customFields?.let {
                customFieldHash[it.issueSectionId.toString() + " " + it.issueTrackingItemsId] = it
            }
            customFields = data
        }
        customFieldAdapter =
            CustomFieldAdapter(
                sectionList,
                projectIssueTrackingProvider,
                this@AddIssueActivity,
                pjIssueId,
                pjIssueIdMobile,
                setCustomFieldData
            )
        binding.rvCustomFields.adapter = customFieldAdapter
    }

    private fun openStatusSelection() {

        val filterDialog = AlertDialog.Builder(this@AddIssueActivity).create()

        val binding = ImpactRootCauseFilterBinding.inflate(layoutInflater, null, false)

        binding.tvLabelFilter.text = getString(R.string.select_status)

        val adapter = ImpactAndRootCauseAdapter(
            impactAndRootCauseList = listOf(
                IssueStatus(
                    id = 1,
                    name = getString(R.string.open),
                    isResolved = false
                ),
                IssueStatus(
                    id = 2,
                    name = getString(R.string.resolved),
                    isResolved = true
                )
            ).apply {
                firstOrNull { it.isResolved == this@AddIssueActivity.binding.issueTracking?.resolvedStatus }?.isSelected =
                    true
            },
            viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_ISSUE_STATUS,
            useMultiSelection = false,
            downloadIcon = downloadIcon
        )

        binding.rvImpactsRootCause.adapter = adapter

        filterDialog.setView(binding.root)

        filterDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.save)
        ) { dialog, _ ->
            adapter.getList().firstOrNull { (it as IssueStatus).isSelected }?.let {
                val selected = (it as IssueStatus)
                this@AddIssueActivity.binding.issueTracking?.resolvedStatus = selected.isResolved
                    ?: false
                this@AddIssueActivity.binding.etIssueStatus.setText(selected.name)
                this@AddIssueActivity.binding.tvResolved.text = if (selected.isResolved == true) {
                    getString(R.string.yes)
                } else {
                    getString(R.string.no)
                }
            }
        }

        filterDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        )
        { dialog, _ ->
            dialog.dismiss()
        }

        filterDialog.show()
        filterDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(R.color.gray_948d8d))
    }

    /*var text:String?=null,
    var number:Int?=null,
    var checkBoxdata:String?=null,
    var selectbox:String?=null,
    var radio:String?=null,
    var phoneNumber:Int?=null,
    var SelectedDate:String?=null,
    var currency:Int?=null,*/
    private fun saveIssue() {
        val issueTracking = binding.issueTracking
        val customFieldValues = ArrayList(customFieldHash.values)
        issueTracking?.let { issue ->
            issue.createdAt = DateFormatter.formatDateTimeForService(Date())
            if (this::assignedTo.isInitialized) {
                issue.assignee = Assignee(assignedTo.usersId.toLong(), assignedTo.name)
            }
            issue.neededByTimeZone = tvAddNeededByTimezone.text.toString()
            if (issueTracking.resolvedStatus) {
                issue.dateResolved = DateFormatter.formatDateTimeForService(Date())
            }
            issue.impactsAndRootCause = (impactsAdapter.getList())
                .filter {
                    (it as MasterImpactAndRootCause).isSelected
                }
                .map {
                    (it as MasterImpactAndRootCause).let { issueImpacts ->
                        IssueImpactAndRootCause(
                            pjIssuesTrackingsId = 0,
                            pjIssuesId = issue.pjIssueId,
                            pjIssuesCauseImpactId = issueImpacts.pjIssueCauseImpactId,
                            type = issueImpacts.impactStatus,
                            pjIssuesTrackingIdMobile = projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE
                            ),
                            iconUrl = issueImpacts.iconUrl,
                            name = issueImpacts.name,
                            createdAt = DateFormatter.formatDateTimeForService(Date())
                        )
                    }
                } + (rootCauseAdapter.getList())
                .filter {
                    (it as MasterImpactAndRootCause).isSelected
                }
                .map {
                    (it as MasterImpactAndRootCause).let { issueImpacts ->
                        IssueImpactAndRootCause(
                            pjIssuesTrackingsId = 0,
                            pjIssuesId = issue.pjIssueId,
                            pjIssuesCauseImpactId = issueImpacts.pjIssueCauseImpactId,
                            type = issueImpacts.impactStatus,
                            pjIssuesTrackingIdMobile = projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE
                            ),
                            iconUrl = issueImpacts.iconUrl,
                            name = issueImpacts.name,
                            createdAt = DateFormatter.formatDateTimeForService(Date())
                        )
                    }
                }

            issue.issuesBreakdown = issueBreakdownAdapter.getCombinedList().apply {
                forEach {
                    if (it.createdAt == null) {
                        it.createdAt = DateFormatter.formatDateTimeForService(Date())
                        it.pjIssuesItemBreakdownIdMobile =
                            projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.ITEM_BREAKDOWN
                            )
                        it.pjIssuesIdMobile = pjIssueIdMobile
                        it.pjIssuesId = pjIssueId
                    }
                }
            }

            fun isValid() = issue.let { issue ->

                var isValid = true

                if (issue.title.isEmpty()) {
                    tilIssueTitle.error = getString(R.string.please_enter_title)
                    isValid = false
                }

                if (issue.impactsAndRootCause.filter { it.type == 0 }.isEmpty()) {
                    binding.tvLabelRootCauseError.isVisible = true
                    isValid = false
                } else {
                    binding.tvLabelRootCauseError.isVisible = false
                }

                isValid = issueBreakdownAdapter.validate() && isValid

                isValid
            }

            if (isValid()) {
                if (!binding.tvAddNeededBy.text.isNullOrEmpty() && !binding.tvAddNeededByTime.text.isNullOrEmpty()
                ) {
                    val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    try {
                        val date: Date? =
                            inputFormat.parse(binding.tvAddNeededByTime.text.toString())
                        issue.neededBy = issue.neededBy.plus(" ").plus(outputFormat.format(date))
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                } else if (binding.tvAddNeededByTime.text.isNullOrEmpty()
                ) {
                    issue.neededBy = null
                }
                val newItem = projectIssueTrackingProvider.storeIssue(issue, customFieldValues)

                newItem?.let {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun editIssue() {
        val issueTracking = binding.issueTracking
        val customFieldValues = ArrayList(customFieldHash.values)

        issueTracking?.let { issue ->
            if (this::assignedTo.isInitialized) {
                issue.assignee = Assignee(assignedTo.usersId.toLong(), assignedTo.name)
            }
            issue.neededByTimeZone = tvAddNeededByTimezone.text.toString()
            if (issue.resolvedStatus && issue.dateResolved.isNullOrEmpty()) {
                issue.dateResolved = DateFormatter.formatDateTimeForService(Date())
            }
            val previousImpactList = previousImpactAndRootCause.filter { it.type == 1 }
            val selectedImpactList =
                impactsAdapter.getList().filter { (it as MasterImpactAndRootCause).isSelected }

            val newImpactList = selectedImpactList.map { newImpact ->
                (newImpact as MasterImpactAndRootCause).let { issueImpacts ->

                    previousImpactList.firstOrNull { it.pjIssuesCauseImpactId == issueImpacts.pjIssueCauseImpactId }
                        ?.let { previousImpact ->
                            previousImpact
                        } ?: kotlin.run {
                        IssueImpactAndRootCause(
                            pjIssuesTrackingsId = 0,
                            pjIssuesId = issue.pjIssueId,
                            pjIssuesCauseImpactId = issueImpacts.pjIssueCauseImpactId,
                            type = issueImpacts.impactStatus,
                            pjIssuesTrackingIdMobile = projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE
                            ),
                            iconUrl = issueImpacts.iconUrl,
                            name = issueImpacts.name,
                            createdAt = DateFormatter.formatDateTimeForService(Date())
                        )
                    }
                }
            }.toCollection(arrayListOf()).also { newList ->
                previousImpactList.forEach { previousImpact ->
                    newList.firstOrNull { newImpact ->
                        previousImpact.pjIssuesCauseImpactId == newImpact.pjIssuesCauseImpactId
                    } ?: kotlin.run {
                        newList.add(
                            previousImpact.apply {
                                deletedAt = DateFormatter.formatDateTimeForService(Date())
                            }
                        )
                    }
                }
            }

            val previousRootCauseList = previousImpactAndRootCause.filter { it.type == 0 }
            val selectedRootCauseList =
                rootCauseAdapter.getList().filter { (it as MasterImpactAndRootCause).isSelected }
            val newRootCauseList = selectedRootCauseList.map { newRootCause ->
                (newRootCause as MasterImpactAndRootCause).let { issueImpacts ->

                    previousRootCauseList.firstOrNull { it.pjIssuesCauseImpactId == issueImpacts.pjIssueCauseImpactId }
                        ?.let { previousImpact ->
                            previousImpact
                        } ?: kotlin.run {
                        IssueImpactAndRootCause(
                            pjIssuesTrackingsId = 0,
                            pjIssuesId = issue.pjIssueId,
                            pjIssuesCauseImpactId = issueImpacts.pjIssueCauseImpactId,
                            type = issueImpacts.impactStatus,
                            pjIssuesTrackingIdMobile = projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE
                            ),
                            iconUrl = issueImpacts.iconUrl,
                            name = issueImpacts.name,
                            createdAt = DateFormatter.formatDateTimeForService(Date())
                        )
                    }
                }
            }.toCollection(arrayListOf()).also { newList ->
                previousRootCauseList.forEach { previousImpact ->
                    newList.firstOrNull { newImpact ->
                        previousImpact.pjIssuesCauseImpactId == newImpact.pjIssuesCauseImpactId
                    } ?: kotlin.run {
                        newList.add(
                            previousImpact.apply {
                                deletedAt = DateFormatter.formatDateTimeForService(Date())
                            }
                        )
                    }
                }
            }

            issue.impactsAndRootCause = newImpactList + newRootCauseList

            issue.issuesBreakdown = issueBreakdownAdapter.getCombinedList().apply {
                forEach {
                    if (it.createdAt == null) {
                        it.createdAt = DateFormatter.formatDateTimeForService(Date())
                        it.pjIssuesItemBreakdownIdMobile =
                            projectIssueTrackingProvider.generateMobileId(
                                ProjectIssueTrackingProvider.MobileIdType.ITEM_BREAKDOWN
                            )
                        it.pjIssuesIdMobile = pjIssueIdMobile
                        it.pjIssuesId = pjIssueId
                    }
                }
            }.toMutableList().also { newList ->
                previousBreakdown.forEach { previous ->
                    newList.firstOrNull {
                        previous.pjIssuesItemBreakdownId == it.pjIssuesItemBreakdownId &&
                                previous.pjIssuesItemBreakdownIdMobile == it.pjIssuesItemBreakdownIdMobile
                    }
                        ?: newList.add(previous)
                }
            }

            fun isValid() = issue.let { issue ->

                var isValid = true

                if (issue.title.isEmpty()) {
                    tilIssueTitle.error = getString(R.string.please_enter_title)
                    isValid = false
                }

                if (issue.impactsAndRootCause.filter { it.type == 0 }.isEmpty()) {
                    binding.tvLabelRootCauseError.isVisible = true
                    isValid = false
                } else {
                    binding.tvLabelRootCauseError.isVisible = false
                }

                isValid = issueBreakdownAdapter.validate() && isValid

                issue.neededBy = binding.tvAddNeededBy.text?.let {
                    if (it.isNotEmpty())
                        binding.tvAddNeededByTime.text?.let {
                            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            var value = ""
                            if (it.isNotEmpty()) {
                                try {
                                    val date: Date? =
                                        inputFormat.parse(binding.tvAddNeededByTime.text.toString())
                                    value = outputFormat.format(date)
                                    //  issue.neededBy = issue.neededBy.plus(" ").plus(outputFormat.format(date))
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                DateFormatter.formatDateForService(
                                    Date(
                                        binding.tvAddNeededBy.text?.split(" ")?.get(0)
                                    )
                                ).toString().plus(" ").plus(value)
                            } else {
                                ""
                            }
                        }
                    else {
                        ""
                    }
                }
                if (binding.tvAddNeededByTime.text.isNullOrEmpty()
                ) {
                    issue.neededBy = null
                }

                isValid
            }

            if (isValid()) {
                val newItem = projectIssueTrackingProvider.updateIssue(issue, customFieldValues)
                newItem?.let {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun showConfirmDiscard() {

        val dialog = AlertDialog.Builder(this@AddIssueActivity).create()

        dialog.setTitle(R.string.confirm)
        dialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving_your_changes))
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
            super.onBackPressed()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.gray_948d8d))
    }

    override fun onBackPressed() {
        when {
            previousIssueItem != binding.issueTracking || isRootCauseOrImpactUpdated() || isBreakDownUpdated() -> {
                showConfirmDiscard()
            }

            else -> super.onBackPressed()
        }
    }

    private fun isRootCauseOrImpactUpdated(): Boolean {

        val newImpactsAndRootCauseList =
            (impactsAdapter.getMasterSelectedList() + rootCauseAdapter.getMasterSelectedList())

        if (previousIssueItem.impactsAndRootCause.size == newImpactsAndRootCauseList.size) {
            if (isEdit) {
                previousIssueItem.impactsAndRootCause.forEach { previousItem ->
                    newImpactsAndRootCauseList.firstOrNull { newItem ->
                        (newItem as MasterImpactAndRootCause).pjIssueCauseImpactId == previousItem.pjIssuesCauseImpactId
                    } ?: run {
                        return true
                    }
                }
            }
            return false
        }

        return true
    }

    private fun isBreakDownUpdated(): Boolean {

        if (previousIssueItem.issuesBreakdown.size == issueBreakdownAdapter.getValidCount()) {

            issueBreakdownAdapter.getCombinedList().forEach {
                if (it.createdAt == null)
                    return true
            }

            return false
        }

        return true
    }

    suspend fun storeIcon(icon: Bitmap, fileName: String, impactAndRootCauseId: Long): String {

        val iconFolder = File(this@AddIssueActivity.filesDir, "Pronovos/ImpactsAndRootCause")
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

        projectIssueTrackingProvider.storeImpactAndRootCauseFilePath(
            iconFile.absolutePath,
            impactAndRootCauseId
        )

        return iconFile.absolutePath
    }

    override fun onResume() {
        super.onResume()
        binding.offlineTextView.isGone = NetworkService.isNetworkAvailable(this@AddIssueActivity)
    }

    override fun toggleOfflineView(event: Boolean?) {
        event?.let {
            isOffline = event

            binding.offlineTextView.isVisible = event

        }
    }

    override fun dataUpdate(issueListResponse: IssueListResponse<IssueListItem>?) {

    }
}
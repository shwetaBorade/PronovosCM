package com.pronovoscm.adapter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pronovoscm.R
import com.pronovoscm.activity.issue_tracking.AddIssueActivity
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ImpactRootCauseFilterBinding
import com.pronovoscm.databinding.ItemAddCustomFieldItemsBinding
import com.pronovoscm.model.response.issueTracking.issues.AdditionalData
import com.pronovoscm.model.view.CustomFields
import com.pronovoscm.model.view.CustomFieldsValues
import com.pronovoscm.model.view.InputTypes
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemTypesCache
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemsCache
import com.pronovoscm.utils.DateFormatter
import com.pronovoscm.utils.dialogs.CustomFieldCheckBoxDialog
import com.pronovoscm.utils.dialogs.CustomFieldRadioDialog
import java.util.*
import org.json.JSONArray
import org.json.JSONObject


class CustomFieldItemAdapter(
    private val itemList: List<IssueTrackingItemsCache>?,
    private val projectIssueTrackingProvider: ProjectIssueTrackingProvider,
    private val context: AddIssueActivity,
    private val setCustomFieldData: (CustomFieldsValues) -> Unit,
    private val pjIssueId: Long?,
    private val pjIssueIdMobile: Long?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dueDate: Date? = null
    var previouslySelectedSelectBox:AdditionalData = AdditionalData()
    var previouslySelectedCheckBox:ArrayList<AdditionalData> = ArrayList()
    var previouslySelectedRadioBox:AdditionalData = AdditionalData()
    val customFields:CustomFieldsValues=CustomFieldsValues()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddCustomFieldItemsViewHolder(
            ItemAddCustomFieldItemsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddCustomFieldItemsViewHolder) {
            itemList?.get(position)?.let { holder.bind(it) }
        }
    }

    inner class AddCustomFieldItemsViewHolder internal constructor(private val binding: ItemAddCustomFieldItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pjTrackingItems: IssueTrackingItemsCache) {
            binding.tvItemHeader.text = pjTrackingItems.itemName
            val itemType =
                projectIssueTrackingProvider.getItemTypes(pjTrackingItems.trackingItemTypesId)
            val textOnEdit = returnValueAccordingToItems(pjTrackingItems)

            if (itemType != null) {
                if (itemType.type.equals(InputTypes.text.toString())) { //TEXT BOX
                    binding.apply {
                        rlDateInput.visibility = View.GONE
                        rlRadioInput.visibility = View.GONE
                        tilTextInput.visibility = View.VISIBLE
                        etTextInput.hint = "Text Input"
                        etTextInput.inputType = InputType.TYPE_CLASS_TEXT
                        if (!textOnEdit.isNullOrEmpty()){
                            etTextInput.setText(textOnEdit)
                            customFields.customFields  =
                                getValueObject(textOnEdit,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                            setCustomFieldData.invoke(customFields)
                        }
                        etTextInput.addTextChangedListener(object :TextWatcher{
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {

                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {

                            }

                            override fun afterTextChanged(value: Editable?) {
                                customFields.customFields =getValueObject(value.toString(),
                                    pjTrackingItems.issueTrackingSectionsId,
                                    pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)

                                setCustomFieldData.invoke(customFields)
                            }
                        })
                    }
                } else if (itemType.type.equals(InputTypes.number.toString()) || itemType.type.equals(InputTypes.phone.toString()) || itemType.type.equals(InputTypes.currency.toString())) { //NUMBER BOX
                    binding.apply {
                        tilTextInput.visibility = View.VISIBLE
                        etTextInput.hint = itemType.label
                        etTextInput.inputType = InputType.TYPE_CLASS_NUMBER
                        rlDateInput.visibility = View.GONE
                        rlRadioInput.visibility = View.GONE
                        if (!textOnEdit.isNullOrEmpty()){
                            etTextInput.setText(textOnEdit)
                            customFields.customFields  =
                                getValueObject(textOnEdit,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                            setCustomFieldData.invoke(customFields)
                        }
                        etTextInput.addTextChangedListener(object :TextWatcher{
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                            }

                            override fun afterTextChanged(value: Editable?) {
                                when (itemType.type) {
                                    InputTypes.number.toString() -> {
                                        customFields.customFields  =
                                            getValueObject(value.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                        setCustomFieldData.invoke(customFields)
                                    }
                                        /*etTextInput.text.toString()*/

                                    InputTypes.phone.toString() -> {
                                        customFields.customFields =
                                            getValueObject(value.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                        setCustomFieldData.invoke(customFields)
                                    }
                                        /*etTextInput.text.toString()*/

                                    else -> {
                                        customFields.customFields  =
                                            getValueObject(value.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                        setCustomFieldData.invoke(customFields)
                                    }
                                /*etTextInput.text.toString()*/
                                }
                            }
                        })
                    }
                } else if (itemType.type.equals(InputTypes.checkbox.toString())||itemType.type.equals(InputTypes.radio.toString())||itemType.type.equals(InputTypes.selectbox.toString())) {
                    val additionalData = getOptionFromAdditionalData(pjTrackingItems.additionalData)//CHECK BOX
                    binding.apply {
                        tilTextInput.visibility = View.GONE
                        rlDateInput.visibility = View.GONE
                        rlRadioInput.visibility = View.VISIBLE
                        tvSelectRadioOption.hint = itemType.label
                        if (!textOnEdit.isNullOrEmpty()){
                            tvSelectRadioOption.text = textOnEdit
                        }
                        val setData = { selectedOption: AdditionalData ->
                            if (itemType.type.equals(InputTypes.radio.toString())){
                                previouslySelectedRadioBox = selectedOption
                                selectedOption.id?.let {
                                    customFields.customFields = getValueObject(it,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)

                                }
                            }else{
                                previouslySelectedSelectBox = selectedOption
                                 selectedOption.id?.let {
                                     customFields.customFields =    getValueObject(it,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                     setCustomFieldData.invoke(customFields)
                                 }
                            }
                                tvSelectRadioOption.text =
                                    selectedOption.name
                        }
                        val setMultipleData = { selectedMultipleOptions:ArrayList<AdditionalData> ->
                            previouslySelectedCheckBox=selectedMultipleOptions
                            val selectedCheckBoxOptionsName = selectedMultipleOptions.map { it.name }.joinToString(",")
                            val selectedCheckBoxOptionsId = selectedMultipleOptions.map { it.id }.joinToString(",")
                            tvSelectRadioOption.text =
                                selectedCheckBoxOptionsName
                            customFields.customFields = getValueObject(selectedCheckBoxOptionsId,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)/*selectedCheckBoxOptionsId*/
                            setCustomFieldData.invoke(customFields)
                        }
                        rlRadioInput.setOnClickListener {
                            if (itemType.type.equals(InputTypes.checkbox.toString())||itemType.type.equals(InputTypes.radio.toString())) {
                                openCheckBoxDialog(
                                    context,
                                    itemType.type,
                                    additionalData,
                                    setData,
                                    setMultipleData,
                                    previouslySelectedCheckBox,
                                    previouslySelectedRadioBox,
                                    previouslySelectedSelectBox
                                )
                            }else{
                                openFilter(additionalData,itemType.type,setData,previouslySelectedSelectBox)
                            }
                        }
                        if (!tvSelectRadioOption.text.isNullOrEmpty()) {
                            when (itemType.type) {
                                InputTypes.checkbox.toString() -> {
                               /*     customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/
                                }
                                InputTypes.radio.toString() -> {
                                 /*   customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/
                                }

                                else ->{
                                  /*  customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/

                                }
                            }
                        }else{
                            when (itemType.type) {
                                InputTypes.checkbox.toString() -> {
                                  /*  customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/
                                }

                                InputTypes.radio.toString() ->{
                                    /*customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/
                                }

                                else ->  {
                                 /*   customFields.customFields =
                                        getValueObject(tvSelectRadioOption.text.toString(),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                                    setCustomFieldData.invoke(customFields)*/
                                }
                            }
                        }
                    }
                }
                else if (itemType.type.equals(InputTypes.date.toString())) { // DATE PICKER
                    binding.apply {
                        tilTextInput.visibility = View.GONE
                        rlDateInput.visibility = View.VISIBLE
                        rlRadioInput.visibility = View.GONE
                        if (!textOnEdit.isNullOrEmpty()){
                            tvSelectedDate.text = if(textOnEdit.isNullOrEmpty() || textOnEdit.equals("-")){
                                "-"
                            }else{
                                DateFormatter.convertformatDateForDrawing(textOnEdit)
                            }
                            customFields.customFields  =
                                getValueObject(textOnEdit,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                            setCustomFieldData.invoke(customFields)
                        }
                        tvSelectedDate.setOnClickListener {
                            onDateClick(binding,pjTrackingItems,itemType)
                        }
                    }
                }
            }
        }

    }
    private fun getValueObject(
        value: String,
        issueTrackingSectionsId: Int,
        issueTrackingItemsId: Int,
        trackingItemTypesId: Int
    ): CustomFields {
        return CustomFields(
            issueTrackingCustomId = 0,
            issueTrackingCustomMobileId = projectIssueTrackingProvider.generateMobileId(
                ProjectIssueTrackingProvider.MobileIdType.IMPACTS_ROOT_CAUSE
            ),
            0,
            issueTrackingSectionsId,
            issueTrackingItemsId,
            trackingItemTypesId,
            value,
            DateFormatter.formatDateTimeForService(Date()),
            DateFormatter.formatDateTimeForService(Date()),
        )
    }

    private fun getOptionFromAdditionalData(additionalData: String) :List<AdditionalData>{
        val gson = Gson()
        val type = object : TypeToken<List<AdditionalData>>() {}.type
        return gson.fromJson(additionalData, type)
    }

    private fun openFilter(
        additionalData: List<AdditionalData>,
        type: String,
        setData: (AdditionalData) -> Unit,
        previouslySelectedSelectBox: AdditionalData
    ) {
        var selectedData = AdditionalData()
        val setSelectionData = { data:AdditionalData->selectedData=data}
        val filterDialog = AlertDialog.Builder(context).create()
        val issuesFilterBinding = ImpactRootCauseFilterBinding.inflate(context.layoutInflater, null, false).apply {
            val adapter = CustomFieldRadioListAdapter(
                context,
                ArrayList(additionalData),
                type,
                setSelectionData,
                previouslySelectedRadioBox,
                previouslySelectedSelectBox
            )
            rvImpactsRootCause.adapter= adapter
            tvLabelFilter.text = "Select Box"
        }

        filterDialog.setView(issuesFilterBinding.root)

        filterDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.save)
        ) { dialog, _ ->
            setData(selectedData)
        }

        filterDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        filterDialog.show()
        filterDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.gray_948d8d))
    }
    fun onDateClick(
        binding: ItemAddCustomFieldItemsBinding,
        pjTrackingItems: IssueTrackingItemsCache,
        itemType: IssueTrackingItemTypesCache
    ) {
        val calendar1: Calendar = GregorianCalendar()
        val mYear = calendar1[Calendar.YEAR]
        val mMonth = calendar1[Calendar.MONTH]
        val mDay = calendar1[Calendar.DATE]
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar: Calendar = GregorianCalendar(
                    year,
                    monthOfYear,
                    dayOfMonth
                )
                dueDate = calendar.time
                binding.tvSelectedDate.text = DateFormatter.formatDateForSubmittals(dueDate)
                customFields.customFields =
                    getValueObject(DateFormatter.formatDate(dueDate),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                setCustomFieldData.invoke(customFields)
            }, mYear, mMonth, mDay
        )
        val calendar: Calendar = GregorianCalendar()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.show()
    }

    private fun openCheckBoxDialog(
        context: AddIssueActivity,
        inputType: String,
        additionalData: List<AdditionalData>,
        setData: (AdditionalData) -> Unit,
        setMultipleData: (ArrayList<AdditionalData>) -> Unit,
        previouslySelectedCheckBox: ArrayList<AdditionalData>,
        previouslySelectedRadioBox: AdditionalData,
        previouslySelectedSelectBox: AdditionalData,
        ) {
        if (inputType == InputTypes.checkbox.toString()) {
            val fm: FragmentManager = context.supportFragmentManager
            val ft = fm.beginTransaction()
            val tagsDialog = CustomFieldCheckBoxDialog(setMultipleData,previouslySelectedCheckBox)
            val bundle = Bundle()
            bundle.putParcelableArrayList("listOfOptions", ArrayList(additionalData))
            tagsDialog.isCancelable = true
            tagsDialog.arguments = bundle
            tagsDialog.show(ft, "")
        } else {
            val fm: FragmentManager = context.supportFragmentManager
            val ft = fm.beginTransaction()
            val tagsDialog = CustomFieldRadioDialog(inputType,context,setData,previouslySelectedRadioBox,previouslySelectedSelectBox)
            val bundle = Bundle()
            bundle.putParcelableArrayList("listOfOptions", ArrayList(additionalData))
            tagsDialog.isCancelable = true
            tagsDialog.arguments = bundle
            tagsDialog.show(ft, "")
        }
    }
    fun returnValueAccordingToItems(pjTrackingItems: IssueTrackingItemsCache): String? {
        val itemType =
            projectIssueTrackingProvider.getItemTypes(pjTrackingItems.trackingItemTypesId)
        if (itemType != null) {
            when (itemType.type.toString()) {
                InputTypes.checkbox.name -> {
                    var returnString = ""
                    var returnId = ""
                    val json =
                        projectIssueTrackingProvider.getAdditionalData(itemType.trackingItemTypesId)
                    val values = projectIssueTrackingProvider.getValues(
                        pjTrackingItems.issueTrackingSectionsId,
                        pjTrackingItems.issueTrackingItemsId,
                        pjIssueId ?: 0, pjIssueIdMobile ?: 0
                    )
                    val additionalData: JSONArray = JSONArray(json)
                    val strings = values?.split(",")
                    for (i in 0 until additionalData.length()) {
                        val objects: JSONObject = additionalData.getJSONObject(i)
                        if (strings?.contains(objects.getString("id")) == true) {
                            returnString += (objects.getString("name").plus(", "))
                            returnId += (objects.getString("id").plus(","))
                            previouslySelectedCheckBox.add(AdditionalData(objects.getString("name"),i.toString(),objects.getString("id")))
                        }
                    }
                    if (returnString.length > 2)
                        returnString = returnString.substring(0, returnString.length - 2)
                    if (returnId.length > 2)
                        returnId = returnString.substring(0, returnString.length - 1)
                    customFields.customFields  =
                        getValueObject(returnId,pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                    setCustomFieldData.invoke(customFields)
                    return returnString
                }

                InputTypes.radio.name,InputTypes.selectbox.name -> {
                    val json =
                        projectIssueTrackingProvider.getAdditionalData(itemType.trackingItemTypesId)
                    val values = projectIssueTrackingProvider.getValues(
                        pjTrackingItems.issueTrackingSectionsId,
                        pjTrackingItems.issueTrackingItemsId,
                        pjIssueId ?: 0, pjIssueIdMobile ?: 0
                    )
                    val additionalData: JSONArray = JSONArray(json)
                    for (i in 0 until additionalData.length()) {
                        val objects: JSONObject = additionalData.getJSONObject(i)
                        if (objects.getString("id").equals(values)) {
                           if (InputTypes.radio.name.equals(itemType.type.toString())){
                               previouslySelectedRadioBox.name=objects.getString("name")
                               previouslySelectedRadioBox.id=objects.getString("id")
                           }else{
                               previouslySelectedSelectBox.name=objects.getString("name")
                               previouslySelectedSelectBox.id=objects.getString("id")

                           }
                            customFields.customFields  =
                                getValueObject(objects.getString("id"),pjTrackingItems.issueTrackingSectionsId,pjTrackingItems.issueTrackingItemsId,itemType.trackingItemTypesId)
                            setCustomFieldData.invoke(customFields)
                            return objects.getString("name")
                        }
                    }
                }
                else -> {}
            }


        }//end of if

        return projectIssueTrackingProvider.getValues(
            pjTrackingItems.issueTrackingSectionsId,
            pjTrackingItems.issueTrackingItemsId,
            pjIssueId ?: 0, pjIssueIdMobile ?: 0
        )
    }
}
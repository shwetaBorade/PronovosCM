package com.pronovoscm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ItemValueCustomFieldItemsBinding
import com.pronovoscm.model.view.InputTypes
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemsCache
import com.pronovoscm.utils.DateFormatter
import java.util.*
import org.json.JSONArray
import org.json.JSONObject


class CustomFieldValueItemAdapter(
    private val itemList: List<IssueTrackingItemsCache>?,
    private val projectIssueTrackingProvider: ProjectIssueTrackingProvider,
    private val pjIssueId: Long? = null,
    private val pjIssueIdMobile: Long? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dueDate: Date? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddCustomFieldItemsViewHolder(
            ItemValueCustomFieldItemsBinding.inflate(
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

    inner class AddCustomFieldItemsViewHolder internal constructor(private val binding: ItemValueCustomFieldItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pjTrackingItems: IssueTrackingItemsCache) {
            binding.tvItemHeader.text = pjTrackingItems.itemName
            binding.tvItemValue.text = returnValueAccordingToItems(pjTrackingItems)
        }
    }

    fun returnValueAccordingToItems(pjTrackingItems: IssueTrackingItemsCache): String? {
        val itemType =
            projectIssueTrackingProvider.getItemTypes(pjTrackingItems.trackingItemTypesId)
        if (itemType != null) {
            when (itemType.type.toString()) {
                InputTypes.checkbox.name -> {
                    var returnString = ""
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
                        }
                    }
                    if (returnString.length > 2)
                        returnString = returnString.substring(0, returnString.length - 2)

                    return returnString
                }

                InputTypes.radio.name, InputTypes.selectbox.name -> {
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
                            return objects.getString("name")
                        }
                    }
                }

                InputTypes.currency.name -> {
                    val currency = projectIssueTrackingProvider.getValues(
                        pjTrackingItems.issueTrackingSectionsId,
                        pjTrackingItems.issueTrackingItemsId,
                        pjIssueId ?: 0, pjIssueIdMobile ?: 0
                    )
                    return if (currency.isNullOrEmpty() || currency.equals("-")) {
                        "-"
                    } else {
                        "$ $currency"
                    }


                }
                InputTypes.date.name -> {
                    val date = projectIssueTrackingProvider.getValues(
                        pjTrackingItems.issueTrackingSectionsId,
                        pjTrackingItems.issueTrackingItemsId,
                        pjIssueId ?: 0, pjIssueIdMobile ?: 0
                    )
                    return if(date.isNullOrEmpty() || date.equals("-")){
                        "-"
                    }else{
                        DateFormatter.convertformatDateForDrawing(date)
                    }
                }
            }


        }//end of if

        return projectIssueTrackingProvider.getValues(
            pjTrackingItems.issueTrackingSectionsId,
            pjTrackingItems.issueTrackingItemsId,
            pjIssueId ?: 0, pjIssueIdMobile ?: 0
        )
    }
}
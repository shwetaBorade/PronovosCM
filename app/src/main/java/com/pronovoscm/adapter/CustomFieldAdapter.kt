package com.pronovoscm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.activity.issue_tracking.AddIssueActivity
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider
import com.pronovoscm.databinding.ItemCustomFieldBinding
import com.pronovoscm.model.view.CustomFieldsValues
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemsCache
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingSectionCache

class CustomFieldAdapter(
    private val sectionList: List<IssueTrackingSectionCache>?,
    private val projectIssueTrackingProvider: ProjectIssueTrackingProvider,
    private val addIssueActivity: AddIssueActivity? = null,
    private val pjIssueId: Long? = null,
    private val pjIssueIdMobile: Long? = null,
    private val setCustomFieldData: (CustomFieldsValues) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsList: List<IssueTrackingItemsCache>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddCustomFieldViewHolder(
            ItemCustomFieldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return sectionList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddCustomFieldViewHolder) {
            if (!sectionList.isNullOrEmpty()) {
                holder.bindSections(sectionList[position])
            }
        }
    }

    inner class AddCustomFieldViewHolder internal constructor(private val binding: ItemCustomFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindSections(pjTrackingSections: IssueTrackingSectionCache) {
            binding.tvSectionTitle.text = pjTrackingSections.sectionName
            itemsList =
                projectIssueTrackingProvider.getItems(pjTrackingSections.issue_tracking_sections_id)
            if (addIssueActivity!=null){
                val itemAdapter = CustomFieldItemAdapter(
                    itemsList, projectIssueTrackingProvider,
                    addIssueActivity,setCustomFieldData,pjIssueId,pjIssueIdMobile
                )
                binding.rvLabelFields.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                binding.rvLabelFields.adapter = itemAdapter
            }else{
                val itemAdapter =
                    CustomFieldValueItemAdapter(itemsList, projectIssueTrackingProvider, pjIssueId, pjIssueIdMobile)
                binding.rvLabelFields.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                binding.rvLabelFields.adapter = itemAdapter
            }
        }
    }
}
package com.pronovoscm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.pronovoscm.chipslayoutmanager.util.log.Log
import com.pronovoscm.databinding.ItemIssuesBinding
import com.pronovoscm.model.view.IssueListItem

class IssueTrackingListAdapter(
        private var issueListItems: MutableList<IssueListItem>,
        private val downloadIcon: (iconUrl: String, impactAndRootCauseId: Long, imageView: ImageView) -> Unit,
        private val onIssueCardClick: (pjIssueId: Long, pjIssueIdMobile: Long) -> Unit
) : RecyclerView.Adapter<IssueTrackingListAdapter.IssueTrackingViewHolder>() {

    fun setIssuesList(issueListItems: MutableList<IssueListItem>) {
        this.issueListItems = issueListItems
        notifyDataSetChanged()
        Log.d("IssueTrackingListAdapter", "setIssueList:" + issueListItems.size)
    }

    fun addIssue(issueListItem: IssueListItem) : Int {
        val index = issueListItems.indexOfLast { it.resolvedStatus.not() }
        if (index != -1) {
            issueListItems.add(index, issueListItem)
            notifyItemInserted(index)
        }
        return index
    }

    fun updateIssue(issueListItem: IssueListItem) {
        val index = issueListItems.indexOfFirst { it.pjIssueId == issueListItem.pjIssueId && it.pjIssueIdMobile == issueListItem.pjIssueIdMobile }
        if (index != -1) {
            issueListItems[index] = issueListItem
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IssueTrackingViewHolder(
            binding = ItemIssuesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: IssueTrackingViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = issueListItems.size

    inner class IssueTrackingViewHolder internal constructor(private val binding: ItemIssuesBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind() {
            val item = issueListItems[absoluteAdapterPosition]
            with(binding) {
                issueTracking = item
                rvImpactsAndCause.adapter = ImpactAndRootCauseAdapter(
                        impactAndRootCauseList = item.impactsAndRootCause,
                        viewType = ImpactAndRootCauseAdapter.VIEW_TYPE_LIST_ISSUE,
                        useMultiSelection = false,
                        downloadIcon = downloadIcon
                )
                projectIssuesCardView.setOnClickListener {
                    onIssueCardClick(item.pjIssueId, item.pjIssueIdMobile)
                }
            }
        }
    }
}
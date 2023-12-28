package com.pronovoscm.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pronovoscm.R
import com.pronovoscm.databinding.ItemImpactAndRootCauseBinding
import com.pronovoscm.databinding.ItemImpactAndRootCauseFilterBinding
import com.pronovoscm.databinding.ItemIssueImpactAndRootCauseBinding
import com.pronovoscm.model.view.ImpactAndRootCause
import com.pronovoscm.model.view.IssueImpactAndRootCause
import com.pronovoscm.model.view.IssueStatus
import com.pronovoscm.model.view.MasterImpactAndRootCause


class ImpactAndRootCauseAdapter(
        private val impactAndRootCauseList: List<ImpactAndRootCause>,
        private val viewType: Int,
        private val useMultiSelection: Boolean,
        private val downloadIcon: (iconUrl: String, impactAndRootCauseId: Long, imageView: ImageView) -> Unit,
        private val toggleRootCauseError: (Int) -> Unit = {},
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val VIEW_TYPE_LIST_ISSUE = 1
        val VIEW_TYPE_VIEW_ISSUE = 2
        val VIEW_TYPE_MASTER = 3
        val VIEW_TYPE_FILTER = 4
        val VIEW_TYPE_ISSUE_STATUS = 5
    }

    private var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == VIEW_TYPE_LIST_ISSUE) {
                IssueImpactAndRootCauseViewHolder(
                        ItemIssueImpactAndRootCauseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            } else if (viewType == VIEW_TYPE_VIEW_ISSUE) {
                ViewIssueImpactAndRootCauseHolder(
                        ItemImpactAndRootCauseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            } else if (viewType == VIEW_TYPE_MASTER) {
                MasterImpactAndRootCauseHolder(
                        ItemImpactAndRootCauseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            } else if (viewType == VIEW_TYPE_FILTER) {
                FilterImpactAndRootCauseHolder(
                        ItemImpactAndRootCauseFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            } else {
                FilterIssueStatusHolder(
                        ItemImpactAndRootCauseFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }

    override fun getItemViewType(position: Int): Int {
        return this.viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IssueImpactAndRootCauseViewHolder -> holder.bind()
            is ViewIssueImpactAndRootCauseHolder -> holder.bind()
            is MasterImpactAndRootCauseHolder -> holder.bind()
            is FilterImpactAndRootCauseHolder -> holder.bind()
            is FilterIssueStatusHolder -> holder.bind()
        }
    }

    override fun getItemCount() = impactAndRootCauseList.size

    fun getList() = impactAndRootCauseList

    fun getMasterSelectedList() = impactAndRootCauseList.filter { (it as MasterImpactAndRootCause).isSelected }

    inner class IssueImpactAndRootCauseViewHolder(private val binding: ItemIssueImpactAndRootCauseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = impactAndRootCauseList[absoluteAdapterPosition] as IssueImpactAndRootCause

            binding.apply {
                if (item.type == 0) {
                    ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(R.color.root_cause_color)))
                    ivIcon.background = AppCompatResources.getDrawable(root.context, R.drawable.root_cause_unselected_state)
                } else {
                    ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(R.color.impact_color)))
                    ivIcon.background = AppCompatResources.getDrawable(root.context, R.drawable.impact_unselected_state)
                }
            }

            if (item.iconUrl.contains("http", ignoreCase = true)) {
                downloadIcon(item.iconUrl, item.pjIssuesCauseImpactId, binding.ivIcon)
            } else {
                if (item.iconUrl.isNullOrEmpty().not()) {
                    val bitmap: Bitmap = BitmapFactory.decodeFile(item.iconUrl)
                    Glide
                        .with(binding.ivIcon)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_default_image)
                        .into(binding.ivIcon)
                }
            }
        }
    }

    inner class ViewIssueImpactAndRootCauseHolder(private val binding: ItemImpactAndRootCauseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = impactAndRootCauseList[absoluteAdapterPosition] as IssueImpactAndRootCause

            /*
            * 0 -> root cause
            * 1 -> impacts
            * */
            binding.apply {
                if (item.type == 0) {
                    ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(R.color.white)))
                    label = item.name
                    background = R.drawable.root_cause_selected_state
                    tvLabel.setTextColor(root.context.getColor(R.color.white))
                } else {
                    ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(R.color.white)))
                    label = item.name
                    background = R.drawable.impact_selected_state
                    tvLabel.setTextColor(root.context.getColor(R.color.white))
                }
            }

            if (item.iconUrl.contains("http", ignoreCase = true)) {
                downloadIcon(item.iconUrl, item.pjIssuesCauseImpactId, binding.ivIcon)
            } else {
                if (item.iconUrl.isNullOrEmpty().not()) {
                    val bitmap: Bitmap = BitmapFactory.decodeFile(item.iconUrl)
                    Glide
                        .with(binding.ivIcon)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_default_image)
                        .into(binding.ivIcon)
                }
            }
        }
    }

    inner class MasterImpactAndRootCauseHolder(private val binding: ItemImpactAndRootCauseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = impactAndRootCauseList[absoluteAdapterPosition] as MasterImpactAndRootCause

            if (useMultiSelection.not() && item.isSelected)
                selectedIndex = absoluteAdapterPosition
            /*
            * 0 -> root cause
            * 1 -> impacts
            * */
            binding.apply {
                when {
                    item.impactStatus == 0 && item.isSelected -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.white,
                                backgroundResource = R.drawable.root_cause_selected_state
                        )
                    }
                    item.impactStatus == 0 && item.isSelected.not() -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.root_cause_color,
                                backgroundResource = R.drawable.root_cause_unselected_state
                        )
                    }
                    item.impactStatus == 1 && item.isSelected -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.white,
                                backgroundResource = R.drawable.impact_selected_state
                        )
                    }
                    item.impactStatus == 1 && item.isSelected.not() -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.impact_color,
                                backgroundResource = R.drawable.impact_unselected_state
                        )
                    }
                }

                mainLayout.setOnClickListener {
                    if (useMultiSelection.not() && selectedIndex != absoluteAdapterPosition) {
                        if (selectedIndex != -1) {
                            (impactAndRootCauseList[selectedIndex] as MasterImpactAndRootCause).isSelected = false
                            notifyItemChanged(selectedIndex)
                        }
                        selectedIndex = absoluteAdapterPosition
                        if (item.impactStatus == 0) {
                            toggleRootCauseError(selectedIndex)
                        }
                    } else {
                        selectedIndex = -1
                        if (item.impactStatus == 0) {
                            toggleRootCauseError(selectedIndex)
                        }
                    }
                    item.isSelected = item.isSelected.not()
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }

            if (item.iconUrl.contains("http", ignoreCase = true)) {
                downloadIcon(item.iconUrl, item.pjIssueCauseImpactId, binding.ivIcon)
            } else {
                if (item.iconUrl.isNullOrEmpty().not()) {
                    val bitmap: Bitmap = BitmapFactory.decodeFile(item.iconUrl)
                    Glide
                        .with(binding.ivIcon)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_default_image)
                        .into(binding.ivIcon)
                }
            }
        }

        private fun ItemImpactAndRootCauseBinding.toggleSelection(
                name: String,
                @ColorRes textTintColor: Int,
                @DrawableRes backgroundResource: Int = R.drawable.root_cause_selected_state
        ) {
            ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(textTintColor)))
            label = name
            background = backgroundResource
            tvLabel.setTextColor(root.context.getColor(textTintColor))
        }
    }

    inner class FilterIssueStatusHolder(private val binding: ItemImpactAndRootCauseFilterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = impactAndRootCauseList[absoluteAdapterPosition] as IssueStatus

            if (useMultiSelection.not() && item.isSelected)
                selectedIndex = absoluteAdapterPosition
            /*
            * 0 -> root cause
            * 1 -> impacts
            * */
            binding.apply {

                ivIcon.isVisible = false

                if (item.isSelected) {
                    binding.toggleSelection(
                            name = item.name,
                            textTintColor = R.color.white,
                            backgroundResource = R.drawable.issue_status_selected_state
                    )
                } else {
                    binding.toggleSelection(
                            name = item.name,
                            textTintColor = R.color.colorPrimary,
                            backgroundResource = R.drawable.issue_status_unselected_state
                    )
                }

                mainLayout.setOnClickListener {
                    if (useMultiSelection.not() && selectedIndex != absoluteAdapterPosition) {
                        if (selectedIndex != -1) {
                            (impactAndRootCauseList[selectedIndex] as IssueStatus).isSelected = false
                            notifyItemChanged(selectedIndex)
                        }
                        selectedIndex = absoluteAdapterPosition
                    }
                    item.isSelected = item.isSelected.not()
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }

        }

        private fun ItemImpactAndRootCauseFilterBinding.toggleSelection(
                name: String,
                @ColorRes textTintColor: Int,
                @DrawableRes backgroundResource: Int = R.drawable.root_cause_selected_state
        ) {
            ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(textTintColor)))
            label = name
            background = backgroundResource
            tvLabel.setTextColor(root.context.getColor(textTintColor))
        }
    }

    inner class FilterImpactAndRootCauseHolder(private val binding: ItemImpactAndRootCauseFilterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = impactAndRootCauseList[absoluteAdapterPosition] as MasterImpactAndRootCause

            if (useMultiSelection.not() && item.isSelected)
                selectedIndex = absoluteAdapterPosition
            /*
            * 0 -> root cause
            * 1 -> impacts
            * */
            binding.apply {
                when {
                    item.impactStatus == 0 && item.isSelected -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.white,
                                iconTint = R.color.white,
                                backgroundResource = R.drawable.issue_status_selected_state
                        )
                    }
                    item.impactStatus == 0 && item.isSelected.not() -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.color_filter_item_label,
                                iconTint = R.color.root_cause_color,
                                backgroundResource = R.drawable.issue_status_unselected_state
                        )
                    }
                    item.impactStatus == 1 && item.isSelected -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.white,
                                iconTint = R.color.white,
                                backgroundResource = R.drawable.issue_status_selected_state
                        )
                    }
                    item.impactStatus == 1 && item.isSelected.not() -> {
                        binding.toggleSelection(
                                name = item.name,
                                textTintColor = R.color.color_filter_item_label,
                                iconTint = R.color.impact_color,
                                backgroundResource = R.drawable.issue_status_unselected_state
                        )
                    }
                }

                mainLayout.setOnClickListener {
                    if (useMultiSelection.not() && selectedIndex != absoluteAdapterPosition) {
                        if (selectedIndex != -1) {
                            (impactAndRootCauseList[selectedIndex] as MasterImpactAndRootCause).isSelected = false
                            notifyItemChanged(selectedIndex)
                        }
                        selectedIndex = absoluteAdapterPosition
                    }
                    item.isSelected = item.isSelected.not()
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }

            if (item.iconUrl.contains("http", ignoreCase = true)) {
                downloadIcon(item.iconUrl, item.pjIssueCauseImpactId, binding.ivIcon)
            } else {
                if(item.iconUrl.isNullOrEmpty().not()) {
                    val bitmap: Bitmap = BitmapFactory.decodeFile(item.iconUrl)
                    Glide
                        .with(binding.ivIcon)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_default_image)
                        .into(binding.ivIcon)
                }
            }
        }

        private fun ItemImpactAndRootCauseFilterBinding.toggleSelection(
                name: String,
                @ColorRes textTintColor: Int,
                @ColorRes iconTint: Int,
                @DrawableRes backgroundResource: Int = R.drawable.root_cause_selected_state
        ) {
            ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(root.context.getColor(iconTint)))
            label = name
            background = backgroundResource
            tvLabel.setTextColor(root.context.getColor(textTintColor))
        }
    }
}
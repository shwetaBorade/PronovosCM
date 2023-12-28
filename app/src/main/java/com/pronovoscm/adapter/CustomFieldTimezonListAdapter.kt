package com.pronovoscm.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pronovoscm.R
import com.pronovoscm.activity.issue_tracking.AddIssueActivity
import com.pronovoscm.databinding.ItemImpactAndRootCauseFilterBinding
import com.pronovoscm.model.response.issueTracking.issues.TimeZone
import com.pronovoscm.model.view.InputTypes

class CustomFieldTimezonListAdapter(
    private val context: AddIssueActivity,
    var listOfOptions: ArrayList<TimeZone>,
    private val inputType: String,
    private var setData: (TimeZone?, ArrayList<TimeZone>?) -> Unit,
    private var  previousSelectedBox: TimeZone,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var selectedRadioOption: String? = null
    private var previouslySelectedRadioOption = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (inputType == InputTypes.radio.toString()) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.select_album_item_list, parent, false)
            CustomFieldRadioListViewHolder(view)
        } else {
            val view = ItemImpactAndRootCauseFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            SelectBoxStatusHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CustomFieldRadioListViewHolder -> holder.bind(listOfOptions[position], position)
            is SelectBoxStatusHolder -> holder.bind(listOfOptions[position], position)
        }
    }

    override fun getItemCount(): Int {
        return listOfOptions.size
    }

    inner class CustomFieldRadioListViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        @JvmField
        @BindView(R.id.tagsView)
        var tagsView: ConstraintLayout = itemView.findViewById(R.id.tagsView)

        @JvmField
        @BindView(R.id.albumTextView)
        var albumTextView: TextView = itemView.findViewById(R.id.albumTextView)

        @JvmField
        @BindView(R.id.albumRadioButton)
        var albumRadioButton: RadioButton = itemView.findViewById(R.id.albumRadioButton)

        init {
            ButterKnife.bind(this, view!!)
        }

        fun bind(value: TimeZone, position: Int) {
            albumRadioButton.isClickable = false
            albumTextView.text = value.name
            albumRadioButton.isChecked =
                position == selectedPosition

            if (value.id.equals(previousSelectedBox.id)) {
                albumRadioButton.isChecked =true}

            tagsView.setOnClickListener {
                previousSelectedBox = TimeZone()
                selectedPosition = position
                selectedRadioOption = value.name
                setData(value, null)

                notifyDataSetChanged()
            }
        }
    }

    inner class SelectBoxStatusHolder(private val binding: ItemImpactAndRootCauseFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(additionalData: TimeZone, position: Int) {
            val isSelected = position == selectedPosition

            binding.apply {

                ivIcon.isVisible = false

                if (isSelected) {
                    additionalData.name?.let {
                        binding.toggleSelection(
                            name = it,
                            textTintColor = R.color.white,
                            backgroundResource = R.drawable.issue_status_selected_state
                        )
                    }
                } else {
                    additionalData.name?.let {
                        binding.toggleSelection(
                            name = it,
                            textTintColor = R.color.colorPrimary,
                            backgroundResource = R.drawable.issue_status_unselected_state
                        )
                    }
                }

                mainLayout.setOnClickListener {
                    if (!isSelected) {
                        val previousSelectedPosition = selectedPosition
                        selectedPosition = position
//                    item.isSelected = item.isSelected.not()
                        setData(additionalData, null)
                        notifyItemChanged(absoluteAdapterPosition)
                        notifyItemChanged(previousSelectedPosition)
                    }
                }
            }

        }

        private fun ItemImpactAndRootCauseFilterBinding.toggleSelection(
            name: String,
            @ColorRes textTintColor: Int,
            @DrawableRes backgroundResource: Int = R.drawable.root_cause_selected_state
        ) {
            ImageViewCompat.setImageTintList(
                ivIcon,
                ColorStateList.valueOf(root.context.getColor(textTintColor))
            )
            label = name
            background = backgroundResource
            tvLabel.setTextColor(root.context.getColor(textTintColor))
        }
    }
}
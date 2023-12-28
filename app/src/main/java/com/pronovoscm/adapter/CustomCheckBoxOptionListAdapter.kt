package com.pronovoscm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pronovoscm.R
import com.pronovoscm.model.response.issueTracking.issues.AdditionalData
import com.pronovoscm.utils.dialogs.CustomFieldCheckBoxDialog

class CustomCheckBoxOptionListAdapter(
    var customFieldCheckBoxDialog: CustomFieldCheckBoxDialog,
    var listOfOptions: ArrayList<AdditionalData>,
    private var setData: (ArrayList<AdditionalData>) -> Unit,
    private var previouslySelectedCheckBox: ArrayList<AdditionalData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val mSelectedOption: ArrayList<AdditionalData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.tags_item_list, parent, false)
        return CustomFieldCheckBoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CustomFieldCheckBoxViewHolder).bind(listOfOptions[position])
    }

    override fun getItemCount(): Int {
        return listOfOptions.size
    }

    inner class CustomFieldCheckBoxViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        @JvmField
        @BindView(R.id.tagTextView)
        var tagTextView: TextView = itemView.findViewById(R.id.tagTextView)

        @JvmField
        @BindView(R.id.tagCheckBox)
        var tagCheckBox: CheckBox = itemView.findViewById(R.id.tagCheckBox)

        @JvmField
        @BindView(R.id.tagsView)
        var tagsView: ConstraintLayout = itemView.findViewById(R.id.tagsView)

        init {
            ButterKnife.bind(this, view!!)
        }

        fun bind(value: AdditionalData) {
            tagCheckBox.isClickable = false
            var isSelected = false
            tagTextView.text = value.name
            for (items in previouslySelectedCheckBox) {
                if (items.id == value.id) {
                    isSelected = true
                    mSelectedOption.add(items)
                }
            }
            tagCheckBox.isChecked = isSelected
            tagsView.setOnClickListener {
                tagCheckBox.isChecked = !tagCheckBox.isChecked
                isSelected=!isSelected
                if (tagCheckBox.isChecked) {
                    mSelectedOption.add(value)
                    setData(mSelectedOption)
                } else {
                    mSelectedOption.remove(value)
                    setData(mSelectedOption)
                }
            }
        }
    }
}
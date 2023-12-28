package com.pronovoscm.adapter

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.databinding.ItemAddIssueBreakdownBinding
import com.pronovoscm.databinding.ItemViewIssueBreakdownBinding
import com.pronovoscm.model.view.IssueBreakdown
import com.pronovoscm.utils.DateFormatter
import java.text.DecimalFormat
import java.util.*

class IssueBreakdownAdapter(
        private val breakdownList: MutableList<IssueBreakdown>,
        private val viewType: Int = TYPE_VIEW,
        private val deleteBreakdown: (index: Int) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var shouldEdit = false

    private val deletedList: MutableList<IssueBreakdown> = mutableListOf()

    private val holders = mutableListOf<AddEditIssueBreakdownViewHolder>()

    companion object {
        val TYPE_ADD = 1
        val TYPE_VIEW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == TYPE_VIEW) {
        ViewIssueBreakdownViewHolder(
                ItemViewIssueBreakdownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    } else {
        AddEditIssueBreakdownViewHolder(
                ItemAddIssueBreakdownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewIssueBreakdownViewHolder -> holder.bind()
            is AddEditIssueBreakdownViewHolder -> {
                holders.add(position, holder)
                holder.bind(position)
            }
        }
    }

    override fun getItemCount() = breakdownList.size

    fun getActualCount() = breakdownList.filter { it.number != 0 }.size

    fun getValidCount() = breakdownList.filterIndexed { index, breakdown ->
        holders[index].validate(checkAll = true) || breakdown.number != 0
    }.size

    override fun getItemViewType(position: Int) = viewType

    fun addItem(issueBreakdown: IssueBreakdown, updateCount: () -> Unit) {
        if (holders.isNotEmpty() && holders.first().validate(checkAll = true, showError = true)) {
            shouldEdit = true
            breakdownList.forEachIndexed { index, it -> it.number = index + 1 }
            breakdownList.add(0, issueBreakdown)
            updateCount()
            notifyDataSetChanged()
        } else if (holders.isEmpty()) {
            shouldEdit = true
            holders.isNotEmpty()
            breakdownList.add(0, issueBreakdown)
            updateCount()
            notifyDataSetChanged()
        }
    }

    fun validate(): Boolean {
        var isValid = true
        holders.forEach {
            isValid = it.validate() && isValid
        }
        return isValid
    }

    fun getList() = breakdownList

    fun getCombinedList() = breakdownList.filterIndexed { index, issueBreakdown -> holders[index].validate(checkAll = true) } + deletedList.also {
        it.forEach { breakdown ->
            breakdown.deletedAt = DateFormatter.formatDateTimeForService(Date())
        }
    }

    fun deleteBreakDown(index: Int, updateCount: () -> Unit) {
        val breakdown = breakdownList[index]
        if (breakdown.createdAt.isNullOrEmpty().not()) {
            deletedList.add(breakdown)
        }
        breakdownList.removeAt(index)
        breakdownList.filter { it.number != 0 }.forEachIndexed { idx, it -> it.number = idx + 1 }
        holders.removeAt(index)
        updateCount()
        notifyDataSetChanged()
    }

    inner class ViewIssueBreakdownViewHolder internal constructor(private val binding: ItemViewIssueBreakdownBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = breakdownList[absoluteAdapterPosition]

            with(binding) {
                issueBreakdown = item
                number = absoluteAdapterPosition + 1
                decimalFormat = DecimalFormat("#,##0.##")
            }
        }
    }

    inner class AddEditIssueBreakdownViewHolder constructor(private val binding: ItemAddIssueBreakdownBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            setIsRecyclable(false)

            val item = breakdownList[position]

            val isEnabled = position == 0 && shouldEdit

            binding.apply {
                issueBreakdown = item

                etAdditionalDays.apply {

                    item.days?.let {
                        val text = if (item.number != 0) {
                            filters = arrayOf(InputFilter.LengthFilter(20))
                            "${item.days} days"
                        } else {
                            filters = arrayOf(InputFilter.LengthFilter(3))
                            item.days.toString()
                        }
                        setText(text)
                    } ?: kotlin.run {
                        filters = arrayOf(InputFilter.LengthFilter(3))
                        text = null
                    }

                    addTextChangedListener {
                        if (it?.toString()?.isNotEmpty() == true) {
                            tilAdditionalDays.error = null
                            item.days = it.toString().toInt()
                        } else {
                            item.days = null
                        }
                    }

                    this.isEnabled = isEnabled
                    this.isFocusable = isEnabled
                    this.setBackgroundResource(if (isEnabled) R.drawable.edit_text_background else R.drawable.disable_rounded_gray_border)
                }

                etDescription.apply {

                    addTextChangedListener { text ->
                        if (text?.isNotEmpty() == true) {
                            tilIssueDescription.error = null
                        }
                    }

                    this.isEnabled = isEnabled
                    this.isFocusable = isEnabled
                    this.setBackgroundResource(if (isEnabled) R.drawable.edit_text_background else R.drawable.disable_rounded_gray_border)
                }

                etCostROM.apply {

                    item.amount?.let {
                        val text = if (item.number != 0) {
                            filters = arrayOf(InputFilter.LengthFilter(50))
                            "$${DecimalFormat("#,##0.##").format(item.amount)}"
                        } else {
                            filters = arrayOf(InputFilter.LengthFilter(10))
                            item.amount.toString()
                        }
                        setText(text)
                    } ?: kotlin.run {
                        filters = arrayOf(InputFilter.LengthFilter(10))
                        text = null
                    }

                    addTextChangedListener { text ->
                        if (text?.isNotEmpty() == true) {
                            item.amount = text.toString().toLong()
                            tilCostROM.error = null
                        } else {
                            item.amount = null
                        }
                    }

                    this.isEnabled = isEnabled
                    this.isFocusable = isEnabled
                    this.setBackgroundResource(if (isEnabled) R.drawable.edit_text_background else R.drawable.disable_rounded_gray_border)
                }
                ivDeleteBreakdown.isVisible = item.createdAt.isNullOrEmpty().not() || position != 0
                ivDeleteBreakdown.setOnClickListener {
                    deleteBreakdown(position)
                }
            }
        }

        fun validate(checkAll: Boolean = false, showError: Boolean = false): Boolean {

            binding.apply {

                val isDescription = etDescription.text.toString().isEmpty()
                val isDays = etAdditionalDays.text.toString().isEmpty()
                val isAmount = etCostROM.text.toString().isEmpty()

                fun showError() {
                    if (isDescription) {
                        tilIssueDescription.error = "Please Enter Description"
                    }
                    if (isAmount) {
                        tilCostROM.error = "Please enter amount"
                    }
                    if (isDays) {
                        tilAdditionalDays.error = "Please enter days"
                    }
                }

                if (checkAll) {
                    if (isDescription && isDays && isAmount) {
                        if (showError) {
                            showError()
                        }
                        return false
                    }

                    if(showError.not()){
                        return true
                    }
                }

                if (isDescription && isDays && isAmount)
                    return true

                showError()

                return (isDescription || isDays || isAmount).not()
            }
        }

        fun showError() {

        }
    }
}
package com.pronovoscm.utils.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pronovoscm.R
import com.pronovoscm.adapter.CustomCheckBoxOptionListAdapter
import com.pronovoscm.model.response.issueTracking.issues.AdditionalData

class CustomFieldCheckBoxDialog(
    private var setMultipleData: (ArrayList<AdditionalData>) -> Unit,
    private val previouslySelectedCheckBox: ArrayList<AdditionalData>
) : DialogFragment(), View.OnClickListener {

    @BindView(R.id.saveTextView)
    lateinit var saveTextView: TextView


    @BindView(R.id.cancelTextView)
    lateinit var cancelTextView: TextView


    @BindView(R.id.titleTextView)
    lateinit var titleTextView: TextView


    @BindView(R.id.tagsRecyclerView)
    lateinit var tagsRecyclerView: RecyclerView


    @BindView(R.id.searchAlbumEditText)
    lateinit var searchAlbumEditText: EditText


    @BindView(R.id.searchView)
    lateinit var searchView: RelativeLayout
    private lateinit var listOfOptions: ArrayList<AdditionalData>
    private lateinit var mCheckBoxAdapter: CustomCheckBoxOptionListAdapter
    private var selectedOptions:ArrayList<AdditionalData> = previouslySelectedCheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Translucent_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.tags_dialog_view, container, false)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeData(view)
        saveTextView.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        if (arguments != null) {
            listOfOptions =
                requireArguments().getParcelableArrayList<AdditionalData>("listOfOptions") as ArrayList<AdditionalData>
        }

        val setData = {selectedMultipleOptions:ArrayList<AdditionalData> -> selectedOptions= selectedMultipleOptions}
        mCheckBoxAdapter =
            CustomCheckBoxOptionListAdapter(this, listOfOptions,setData,previouslySelectedCheckBox)
        tagsRecyclerView.layoutManager = LinearLayoutManager(activity)
        tagsRecyclerView.adapter = mCheckBoxAdapter
        titleTextView.text = "Check Box"
        searchAlbumEditText.hint = getString(R.string.search_here)
    }

    private fun initializeData(view: View) {
        saveTextView = view.findViewById(R.id.saveTextView)

        cancelTextView = view.findViewById(R.id.cancelTextView)

        titleTextView = view.findViewById(R.id.titleTextView)

        tagsRecyclerView = view.findViewById(R.id.tagsRecyclerView)

        searchAlbumEditText = view.findViewById(R.id.searchAlbumEditText)

        searchView = view.findViewById(R.id.searchView)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.saveTextView -> {
                setMultipleData(selectedOptions)
                dismiss()
            }

            R.id.cancelTextView -> dismiss()
        }
    }
}
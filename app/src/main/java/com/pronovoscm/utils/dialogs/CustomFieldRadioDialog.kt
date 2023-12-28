package com.pronovoscm.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pronovoscm.R
import com.pronovoscm.activity.issue_tracking.AddIssueActivity
import com.pronovoscm.adapter.CustomFieldRadioListAdapter
import com.pronovoscm.model.response.issueTracking.issues.AdditionalData
import com.pronovoscm.model.view.InputTypes

class CustomFieldRadioDialog(
    private val inputType: String,
    private val context: AddIssueActivity,
    private var setDataOnSave: (AdditionalData) -> Unit,
    private val previouslySelectedRadioBox: AdditionalData,
    private val previouslySelectedSelectBox: AdditionalData
) : DialogFragment(), View.OnClickListener {

    @BindView(R.id.saveTextView)
    lateinit var saveTextView: TextView


    @BindView(R.id.cardView)
    lateinit var cardView: CardView


    @BindView(R.id.titleTextView)
    lateinit var titleTextView: TextView


    @BindView(R.id.cancelTextView)
    lateinit var cancelTextView: TextView


    @BindView(R.id.tagsRecyclerView)
    lateinit var tagsRecyclerView: RecyclerView


    @BindView(R.id.searchView)
    lateinit var searchView: RelativeLayout


    @BindView(R.id.searchAlbumEditText)
    lateinit var searchAlbumEditText: EditText


    @BindView(R.id.buttonView)
    lateinit var buttonView: LinearLayout
    private lateinit var listOfOptions: ArrayList<AdditionalData>
    private lateinit var mRadioListAdapter: CustomFieldRadioListAdapter
    private var selectedRadioOption:AdditionalData = previouslySelectedRadioBox
    private var selectedSelectBoxOption:AdditionalData = previouslySelectedRadioBox
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
            listOfOptions = requireArguments().getParcelableArrayList<AdditionalData>("listOfOptions") as ArrayList<AdditionalData>
        }
        val setData = {selectedData:AdditionalData-> if (inputType == InputTypes.radio.toString()) {
            selectedRadioOption = selectedData
        }else{
            selectedSelectBoxOption = selectedData
        }}
        mRadioListAdapter = CustomFieldRadioListAdapter(
            context,
            listOfOptions,
            inputType,
            setData,
            previouslySelectedRadioBox,
            previouslySelectedSelectBox
        )
        tagsRecyclerView.layoutManager = LinearLayoutManager(activity)
        tagsRecyclerView.adapter = mRadioListAdapter
        titleTextView.text = "Radio Buttons"
        searchAlbumEditText.hint = getString(R.string.search_here)
    }

    private fun initializeData(view: View) {
        saveTextView = view.findViewById(R.id.saveTextView)

        cardView = view.findViewById(R.id.cardView)

        titleTextView = view.findViewById(R.id.titleTextView)

        cancelTextView = view.findViewById(R.id.cancelTextView)

        tagsRecyclerView = view.findViewById(R.id.tagsRecyclerView)

        searchView = view.findViewById(R.id.searchView)

        searchAlbumEditText = view.findViewById(R.id.searchAlbumEditText)

        buttonView = view.findViewById(R.id.buttonView)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.saveTextView -> {
                setDataOnSave.let {
                    if (inputType == InputTypes.radio.toString()) {
                        it(selectedRadioOption)
                    }else{
                        it(selectedSelectBoxOption)
                    }
                }
                dismiss()
            }

            R.id.cancelTextView -> {
                this.dismiss()
            }
        }
    }
}
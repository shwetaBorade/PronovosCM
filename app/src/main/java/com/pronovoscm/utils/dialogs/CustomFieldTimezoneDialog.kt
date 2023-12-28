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
import com.pronovoscm.adapter.CustomFieldTimezonListAdapter
import com.pronovoscm.model.response.issueTracking.issues.TimeZone
import com.pronovoscm.model.view.OptionList

class CustomFieldTimezoneDialog(
    private val inputType: String,
    private val context: AddIssueActivity,
    private var setData: (TimeZone?, ArrayList<TimeZone>?) -> Unit,
    private val previousSelectedBox: TimeZone
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
    private lateinit var listOfOptions: ArrayList<TimeZone>
    private lateinit var mRadioListAdapter: CustomFieldTimezonListAdapter
    private var getSelectedRadioOptions: OptionList = OptionList(0, "")
    private var getSelectedPosition: Int = -1
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
        val timezone = ArrayList<TimeZone>()
        timezone.add(TimeZone("EDT","EDT"))
        timezone.add(TimeZone("CDT","CDT"))
        timezone.add(TimeZone("MDT","MDT"))
        timezone.add(TimeZone("MST","MST"))
        timezone.add(TimeZone("PDT","PDT"))
        timezone.add(TimeZone("AKDT","AKDT"))
        timezone.add(TimeZone("HDT","HDT"))
        timezone.add(TimeZone("HST","HST"))
        saveTextView.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
        if (arguments != null) {
            listOfOptions = timezone
                //requireArguments().getParcelableArrayList<AdditionalData>("listOfOptions") as ArrayList<AdditionalData>
        }
        mRadioListAdapter = CustomFieldTimezonListAdapter(context, listOfOptions, inputType, setData,previousSelectedBox)
        tagsRecyclerView.layoutManager = LinearLayoutManager(activity)
        tagsRecyclerView.adapter = mRadioListAdapter
        titleTextView.text = "Select Timezone"
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
                dismiss()
            }

            R.id.cancelTextView -> {
                this.dismiss()
            }
        }
    }
}
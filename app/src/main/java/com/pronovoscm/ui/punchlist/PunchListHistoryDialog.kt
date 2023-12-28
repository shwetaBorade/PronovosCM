package com.pronovoscm.ui.punchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.fragments.PunchlistFragment
import com.pronovoscm.persistence.domain.PunchlistAssignee
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments
import com.pronovoscm.persistence.repository.PunchListRepository
import com.pronovoscm.ui.punchlist.adapter.PunchListHistoryAdapter

class PunchListHistoryDialog(
    private val fragment: PunchlistFragment,
    private val punchListHistories: MutableList<out PunchListHistoryDb>,
    private val punchListAssignees: MutableList<out PunchlistAssignee>,
    private val mPunchListRepository: PunchListRepository,
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Translucent_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.layout_dialog_fragment_punchlist_history,
            container,
            false
        )
        isCancelable = true
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false,)
        var historyAdapter = PunchListHistoryAdapter(
            fragment,
            punchListHistories = punchListHistories,
            punchListAssignees,  mPunchListRepository)
        recyclerView?.adapter = historyAdapter
        val cancel = view.findViewById<Button>(R.id.historyCancelId)
        cancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
}
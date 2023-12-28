package com.pronovoscm.ui.punchlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pronovoscm.R
import com.pronovoscm.fragments.PunchlistFragment
import com.pronovoscm.model.PunchListStatus
import com.pronovoscm.model.response.login.LoginResponse
import com.pronovoscm.persistence.domain.PunchlistAssignee
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb
import com.pronovoscm.persistence.repository.PunchListRepository
import com.pronovoscm.utils.DateFormatter
import com.pronovoscm.utils.SharedPref

class PunchListHistoryAdapter(
    private val fragment: PunchlistFragment,
    val punchListHistories: List<PunchListHistoryDb>,
    private val punchListAssignees: List<PunchlistAssignee>,
    private val mPunchListRepository: PunchListRepository,
    ):RecyclerView.Adapter<PunchListHistoryAdapter.HistoryHolder>(){
    inner class HistoryHolder(val view:View) : RecyclerView.ViewHolder(view) {
        var statusIcon: ImageView = view.findViewById(R.id.statusIconImgId)
        var statusText: TextView = view.findViewById(R.id.statusTxtId)
        var statusSubTitle: TextView = view.findViewById(R.id.statusSubTitleTxtId)
        var rejectComment: TextView = view.findViewById(R.id.rejectCommentTxtId)
        val rejectAttachmentView: RecyclerView = view.findViewById(R.id.rejectAttachmentId)

        fun bind(
            punchListHistoryDb: PunchListHistoryDb,
            punchListAssignees: List<PunchlistAssignee>
        ) {
            val loginResponse: LoginResponse? = Gson().fromJson(
                SharedPref.getInstance(view.context).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse::class.java)
            statusText.text = PunchListStatus.getStatus(punchListHistoryDb.status).toString()
            if(punchListHistoryDb.status == PunchListStatus.Open.value){
                statusText.text = view.resources.getString(R.string.show_history_status)
            }
            if(punchListHistoryDb.status == PunchListStatus.Complete.value){
                statusText.text = view.resources.getString(R.string.punchlist_status_completed)
                statusIcon.setColorFilter(ContextCompat.getColor(view.context,R.color.blue_color_picker))
            }
            if(punchListHistoryDb.status == PunchListStatus.Rejected.value) {
                statusIcon.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        R.color.recomplete_color
                    )
                )
                statusText.text = PunchListStatus.getStatus(punchListHistoryDb.status).toString()
                    .plus(":")
                rejectComment.visibility = View.VISIBLE
                rejectComment.text = punchListHistoryDb.comments
            }
            else {
                rejectComment.visibility = View.INVISIBLE
                statusIcon.setColorFilter(ContextCompat.getColor(view.context, R.color.gray_948d8d))
            }

            /*loginResponse?.userDetails?.let {
                if(it.users_id == punchListHistoryDb.createdBy.toInt())
                {
                    statusSubTitle.text = view.resources.getString(R.string.punchlist_history_sub_title, it.firstname.plus(" ").plus(it.lastname),
                        DateFormatter.formatDateInMMDDYYYY(punchListHistoryDb.createdAt))
                }else {
                    for(assignee in punchListAssignees){
                        if(assignee.usersId == punchListHistoryDb.createdBy.toInt()) {
                            val createdName = assignee.name
                            statusSubTitle.text = view.resources.getString(R.string.punchlist_history_sub_title, createdName,
                                DateFormatter.formatDateInMMDDYYYY(punchListHistoryDb.createdAt))
                            break
                        }
                    }
                }
            }*/
            statusSubTitle.text = view.resources.getString(R.string.punchlist_history_sub_title, punchListHistoryDb.createdByName,
                DateFormatter.formatDateInMMDDYYYY(punchListHistoryDb.createdAt))

            if(punchListHistoryDb.status == PunchListStatus.Rejected.value){
                statusText.setTextColor(ContextCompat.getColor(view.context,R.color.red))
                statusIcon.setColorFilter(ContextCompat.getColor(view.context,R.color.red))
//                statusSubTitle.setTextColor(ContextCompat.getColor(view.context,R.color.red))
            }else if(punchListHistoryDb.status == PunchListStatus.Approved.value) {
                statusIcon.setColorFilter(ContextCompat.getColor(view.context,R.color.green_color_picker))
                statusText.setTextColor(ContextCompat.getColor(view.context,R.color.green_color_picker))
            }

            val attachments = mPunchListRepository.getPunchListRejectHistoryAttachments(
                punchListHistoryDb.userId,
                punchListHistoryDb.punchListId.toLong(),
                punchListHistoryDb.punchListAuditsId,
                punchListHistoryDb.punchListAuditsMobileId
            )
            rejectAttachmentView.layoutManager = GridLayoutManager(view.context,4)
            if(attachments.size > 0 ) {
                rejectAttachmentView.adapter = RejectReasonAttachmentOnHistoryAdaptor(fragment,attachments)
               rejectAttachmentView.visibility = View.VISIBLE
                /*if(punchListHistoryDb.status == PunchListStatus.Rejected.value) {
                    rejectAttachmentView.visibility = View.VISIBLE
                }else {
                    rejectAttachmentView.visibility = View.GONE
                }*/
            }else {
                rejectAttachmentView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PunchListHistoryAdapter.HistoryHolder {
        return  HistoryHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_punchlist_history, parent, false))
    }

    override fun getItemCount(): Int {
        return punchListHistories.size
    }

    override fun onBindViewHolder(holder: PunchListHistoryAdapter.HistoryHolder, position: Int) {
        holder.bind(punchListHistoryDb = punchListHistories[position], punchListAssignees)
    }
}
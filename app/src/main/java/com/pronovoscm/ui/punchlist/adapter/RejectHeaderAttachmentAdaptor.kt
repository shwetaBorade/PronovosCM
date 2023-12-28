package com.pronovoscm.ui.punchlist.adapter

import android.Manifest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.activity.BaseActivity
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments
import com.pronovoscm.ui.punchlist.RejectReasonCallback

/**
 * Create the initial item of the recycler header view.
 */
internal class RejectHeaderAttachmentAdaptor(
    val rejectReasonCallback: RejectReasonCallback,
    val punchListRejectReasonAttachments: List<PunchListRejectReasonAttachments>
) :
    RecyclerView.Adapter<RejectHeaderAttachmentAdaptor.RejectHeaderHolder>() {

    internal inner class RejectHeaderHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val bgImage: ImageView = view.findViewById(R.id.backgroundImageView)
        private var colum = arrayOf<String>(
            BaseActivity.getExternalPermission(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        fun bind() {
            bgImage.setOnClickListener(View.OnClickListener {
                rejectReasonCallback.openImageCallback()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RejectHeaderHolder {
        return RejectHeaderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.attachment_header_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RejectHeaderHolder, position: Int) {
        holder.bind()
    }


}
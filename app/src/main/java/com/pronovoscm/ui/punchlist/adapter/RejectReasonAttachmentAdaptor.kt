package com.pronovoscm.ui.punchlist.adapter

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.model.PDFSynEnum
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments
import com.pronovoscm.ui.punchlist.RejectReasonCallback
import com.pronovoscm.utils.FileUtils
import com.pronovoscm.utils.ui.LoadImageInBackground
import com.pronovoscm.utils.ui.LoadImageRejectReasonInBackground
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.RejectedExecutionException

class RejectReasonAttachmentAdaptor(
    val rejectReasonCallback: RejectReasonCallback,
    private var punchListRejectReasonAttachments: List<PunchListRejectReasonAttachments>
    ):
RecyclerView.Adapter<RejectReasonAttachmentAdaptor.RejectReasonHolder>(){

    inner class RejectReasonHolder(view: View) : RecyclerView.ViewHolder(view) {
        var attachmentImageView: ImageView = view.findViewById(R.id.attachmentImageView)
        var backgroundImageView: ImageView = view.findViewById(R.id.backgroundImageView)
        var imageViewRemove: ImageView = view.findViewById(R.id.imageViewRemove)
        var attachmentProgressBar: ProgressBar = view.findViewById(R.id.attachmentProgressBar)

        fun bind(punchListRejectReasonAttachments: PunchListRejectReasonAttachments){

            imageViewRemove.visibility = View.GONE


            backgroundImageView.visibility = View.VISIBLE
            attachmentProgressBar.visibility = View.VISIBLE
            attachmentImageView.visibility = View.VISIBLE

            var uri: URI? = null
            if (punchListRejectReasonAttachments.type.equals("png", ignoreCase = true)
                || punchListRejectReasonAttachments.type.equals("jpg", ignoreCase = true)
                || punchListRejectReasonAttachments.type.equals("jpeg", ignoreCase = true)
            ) {
                try {
                    uri = URI(punchListRejectReasonAttachments.attachmentPath)
                    val segments = uri.path.split("/")
                   /* val segments = uri.path.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()*/
                    val imageName = segments[segments.size - 1]

//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workDetailsAttachments.getAttachmentPath(), workDetailsAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
                    val filePath = attachmentImageView.context.filesDir.absolutePath + "/Pronovos"
//                    val params = arrayOf(punchListRejectReasonAttachments.attachmentPath, filePath)
                    val params = arrayOf(
                        punchListRejectReasonAttachments.attachmentPath,
                        filePath,
                        attachmentImageView.context,
                        attachmentImageView
                    )
                    val imgFile = File("$filePath/$imageName")
                    if (!imgFile.exists()) {
                        try {
                            LoadImageRejectReasonInBackground(object : LoadImageRejectReasonInBackground.Listener {
                                override fun onImageDownloaded(bitmap: Bitmap) {
                                    backgroundImageView.visibility = View.GONE
                                    attachmentProgressBar.visibility = View.GONE
                                    attachmentImageView.setImageResource(
                                        FileUtils.getFileImage(
                                            punchListRejectReasonAttachments.type
                                        )
                                    )
                                    punchListRejectReasonAttachments.fileStatus = PDFSynEnum.SYNC.ordinal
//                                    mPunchListRepository.updateAttachment(punchListRejectReasonAttachments)
//                                    rejectReasonCallback.updatePunchListReasonAttachment(punchListRejectReasonAttachments)

                                }

                                override fun onImageDownloadError() {
                                    attachmentProgressBar.visibility = View.GONE
                                }
                            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params)
                        } catch (e: RejectedExecutionException) {
                            e.printStackTrace()
                        }
                    } else {

                        attachmentProgressBar.visibility = View.GONE
                        attachmentImageView.setImageResource(FileUtils.getFileImage(punchListRejectReasonAttachments.type))
                        attachmentImageView.setOnClickListener(View.OnClickListener { rejectReasonCallback.openAttachImage(absoluteAdapterPosition) })
                        punchListRejectReasonAttachments.fileStatus = PDFSynEnum.SYNC.ordinal
//                        rejectReasonCallback.updatePunchListReasonAttachment(punchListRejectReasonAttachments)
                    }
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            } else {
                attachmentProgressBar.visibility = View.GONE
                if (punchListRejectReasonAttachments.fileStatus != null && punchListRejectReasonAttachments.fileStatus == PDFSynEnum.SYNC.ordinal) attachmentImageView.setImageResource(
                    FileUtils.getFileImage(punchListRejectReasonAttachments.type)
                ) else {
                    // download file
                    attachmentProgressBar.visibility = View.VISIBLE
                    attachmentImageView.visibility = View.GONE
//                    downLoadPjDocumentFile(punchListRejectReasonAttachments, attachmentImageView, false)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RejectReasonHolder {
            return  RejectReasonHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.work_details_attachment_item_list, parent, false))
    }

    override fun getItemCount(): Int {
        return punchListRejectReasonAttachments.size
    }

    override fun onBindViewHolder(holder: RejectReasonHolder, position: Int) {
        holder.bind(punchListRejectReasonAttachments = punchListRejectReasonAttachments[position])
    }
}
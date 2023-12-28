package com.pronovoscm.ui.punchlist.adapter

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.fragments.PunchlistFragment
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments
import com.pronovoscm.utils.dialogs.RejectReasonAttachmentDialog
import java.io.File

class RejectReasonAttachmentOnHistoryAdaptor(
    private val fragment: PunchlistFragment,
    private val rejectReasonAttachments: List<PunchListRejectReasonAttachments>
): RecyclerView.Adapter<RejectReasonAttachmentOnHistoryAdaptor.RejectReasonHistoryHolder>() {

    inner class RejectReasonHistoryHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.attachmentImgId)
        val fileType: TextView = view.findViewById(R.id.attachFileTxtId)

        fun bind(rejectReasonHistoryAttachments: PunchListRejectReasonAttachments) {
            fileType.text = rejectReasonHistoryAttachments.type


//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workDetailsAttachments.getAttachmentPath(), workDetailsAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
           /* val filePath: String =
                view.getContext().getFilesDir().getAbsolutePath() + "/Pronovos"
            val uri = URI(rejectReasonHistoryAttachments.attachmentPath)
            val segments = uri.getPath().split("/")
            val imageName = segments[segments.size - 1]
            val params = arrayOf(rejectReasonHistoryAttachments.attachmentPath, filePath)
            val imgFile = File("$filePath/$imageName")
            if (!imgFile.exists()) {
                try {
                    LoadImageInBackground(object : LoadImageInBackground.Listener {
                        override fun onImageDownloaded(bitmap: Bitmap) {

                        }

                        override fun onImageDownloadError() {
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *params)
                } catch (e: RejectedExecutionException) {
                    e.printStackTrace()
                }
            }*/

//            downloadImage(rejectReasonHistoryAttachments.attachmentPath)

            view.setOnClickListener(View.OnClickListener {
                Log.d("King", "bind: ${rejectReasonHistoryAttachments.attachmentPath}")
                openImage(view, rejectReasonHistoryAttachments.attachmentPath,adapterPosition)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RejectReasonAttachmentOnHistoryAdaptor.RejectReasonHistoryHolder {
        return  RejectReasonHistoryHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.history_attachment_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return rejectReasonAttachments.size
    }

    override fun onBindViewHolder(holder: RejectReasonAttachmentOnHistoryAdaptor.RejectReasonHistoryHolder, position: Int) {
        holder.bind(rejectReasonHistoryAttachments = rejectReasonAttachments[position])
    }

    private fun openImage(view: View,path:String, position:Int) {

       /* val name = path.substring(
            path.lastIndexOf("/") + 1 )
        val file = File(Environment.DIRECTORY_PICTURES+File.separator+name)*/
        val fm = fragment.activity?.supportFragmentManager
        val ft = fm?.beginTransaction()
        val attachmentDialog = RejectReasonAttachmentDialog()
        val bundle = Bundle()
//        Log.d("Manya", "openImage: ${file.absolutePath}")
        bundle.putString("attachment_path", path)

        bundle.putString("title_text", view.resources.getString(R.string.punch_list_history))
        attachmentDialog.arguments = bundle
        attachmentDialog.show(ft!!, "")
       /* val fm: FragmentManager? = fragment.activity?.supportFragmentManager
        val openOpenImageFromURL: OpenImageFromURL = OpenImageFromURL(path)
        fm?.let {
            openOpenImageFromURL.show(it,"fragment_open_image")
        }*/
//        openImageFrmoServer.show(fm!!, "fragment_open_image")
    }

}
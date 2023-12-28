package com.pronovoscm.ui.punchlist

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.BaseBundle
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pronovoscm.R
import com.pronovoscm.activity.BaseActivity
import com.pronovoscm.activity.PronovosCameraActivity
import com.pronovoscm.fragments.PunchlistFragment
import com.pronovoscm.persistence.domain.PunchlistDb
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments
import com.pronovoscm.persistence.repository.PunchListRepository
import com.pronovoscm.ui.punchlist.adapter.RejectHeaderAttachmentAdaptor
import com.pronovoscm.ui.punchlist.adapter.RejectReasonAttachmentAdaptor
import com.pronovoscm.utils.FileUtils
import com.pronovoscm.utils.dialogs.AttachmentDeleteInterface
import com.pronovoscm.utils.dialogs.AttachmentDialog
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

class RejectPunchListDialog(
    val isFragment: Boolean,
    val rejectReasonCallback: RejectReasonOnFragmentCallback,
    val punchlist: PunchlistDb,
    val punchListRepository: PunchListRepository
) : DialogFragment(),
    RejectReasonCallback, AttachmentDeleteInterface {


    private var punchListRejectReasonAttachments = mutableListOf<PunchListRejectReasonAttachments>()
    var recyclerView: RecyclerView? = null
    private var concatAdapter: ConcatAdapter = ConcatAdapter()
    private var rejectReasonAttachmentAdaptor =
        RejectReasonAttachmentAdaptor(this, punchListRejectReasonAttachments)
    private lateinit var comment: TextView


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
            R.layout.layout_dialog_fragment_punchlist_reject_reason,
            container,
            false
        )
        isCancelable = true
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rejectReasonLabel = view.findViewById<TextView>(R.id.reasonLableId)
        val errorTxt = view.findViewById<TextView>(R.id.errorTxtId)
        comment = view.findViewById(R.id.rejectReasonId)
        recyclerView = view.findViewById<RecyclerView>(R.id.rejectReasonAttachmentsView)
        recyclerView?.layoutManager = GridLayoutManager(recyclerView?.context, 4)

        rejectReasonLabel.text =
            getString(R.string.punchlist_reject_reason_lable, punchlist.descriptions.toString())
        val rejectHeaderAttachmentAdaptor =
            RejectHeaderAttachmentAdaptor(this, punchListRejectReasonAttachments)
        rejectReasonAttachmentAdaptor =
            RejectReasonAttachmentAdaptor(this, punchListRejectReasonAttachments)
        concatAdapter = ConcatAdapter(rejectHeaderAttachmentAdaptor, rejectReasonAttachmentAdaptor)
        recyclerView?.adapter = concatAdapter

        val cancel = view.findViewById<TextView>(R.id.cancelReasonView)
        cancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        val save = view.findViewById<TextView>(R.id.saveReasonView)
        save.setOnClickListener(View.OnClickListener {
            if (comment.text.isEmpty()) {
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Please enter the comment."
            } else {
                saveRecordInDb(comment.text.toString())
            }

        })
    }

    override fun openImageCallback() {
//        selectFromGallery()
        selectImage()
    }

    private fun selectFromGallery() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(BaseActivity.getExternalPermission()),
            1
        )
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_PICTURE)
    }

    private fun openCamera() {
        val captureIntent = Intent(
            activity,
            PronovosCameraActivity::class.java
        ).putExtra("totalImageCount", 1)
        startActivityForResult(captureIntent, CAPTURE_IMAGE)
        /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, TAKE_PICTURE)*/
    }


    override fun updatePunchListReasonAttachment(punchListRejectReasonAttachments: PunchListRejectReasonAttachments) {
//        punchListRepository.updatePunchListReasonAttachment(punchListRejectReasonAttachments)
    }

    override fun onDelete(position: Int) {

    }

    override fun openAttachImage(position: Int) {
        val fm = requireActivity().supportFragmentManager
        val ft = fm.beginTransaction()
        val attachmentDialog = AttachmentDialog()
        val bundle = Bundle()
        bundle.putString(
            "attachment_path",
            punchListRejectReasonAttachments[position - 1].attachmentPath
        )
        bundle.putInt("image_position", 1)
        bundle.putString("title_text", "Rejected PunchList")

        attachmentDialog.arguments = bundle
        attachmentDialog.show(ft, "")
    }

    private fun saveRecordInDb(comment: String) {
        Log.d(TAG, "galleryView: ${punchListRejectReasonAttachments.size}")
        punchListRepository.updatePunchListReasonAttachment(
            punchlist,
            comment,
            punchListRejectReasonAttachments
        )
        dismiss()
        if (rejectReasonCallback != null && isFragment) {
            rejectReasonCallback.rejectReasonCallback()
        } else {
            if (activity is DialogInterface.OnDismissListener) {
                (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SELECT_PICTURE -> saveFile(data = data.data as Uri, null)
                CAPTURE_IMAGE -> captureResult(data)
                TAKE_PICTURE -> saveFile(captureData = data)
            }
        }
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_from_library)
        )
        val title = TextView(context)
        title.setText(R.string.add_photo)
        title.setBackgroundColor(Color.BLACK)
        title.setPadding(10, 15, 15, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 22f
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setItems(
            items
        ) { dialog: DialogInterface, item: Int ->
            if (items[item] == getString(R.string.take_photo)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && BaseActivity.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requireActivity().requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            BaseActivity.getExternalPermission()
                        ), PunchlistFragment.FILECAMERA_REQUEST_CODE
                    )
                } else {
                    openCamera()
                }
            } else if (items[item] == getString(R.string.choose_from_library)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    BaseActivity.checkSelfPermission(
                        requireActivity(),
                        BaseActivity.getExternalPermission()
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requireActivity().requestPermissions(
                        arrayOf(BaseActivity.getExternalPermission()),
                        PERMISSION_READ_REQUEST_CODE
                    )
                } else {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    intent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(
                            intent,
                            getString(R.string.select_picture)
                        ), SELECT_PICTURE
                    )
                }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun captureResult(data: Intent?) {
        val punchListRejectReasonAttachment = PunchListRejectReasonAttachments()
        var fileName: String = ""
        if (data != null) {
            punchListRejectReasonAttachment.attachmentPath = data.getStringExtra("image_path")
            val names = data.getStringExtra("image_path")?.split("/")
            names?.let {
                fileName = it[it.size - 1]
            }
        }
        punchListRejectReasonAttachment.isAwsSync = false
//                        punchListRejectReasonAttachments.punchListIdMobile = punchListMobileId
        punchListRejectReasonAttachment.usersId = punchlist.userId
        punchListRejectReasonAttachment.punchListId = punchlist.punchlistId
        punchListRejectReasonAttachment.rejectAttachmentId = 0
        punchListRejectReasonAttachment.punchListAuditsId = 0
        punchListRejectReasonAttachment.originalName = fileName
        //                    punchListAttachment.setAttachmentIdMobile(0L);
//                        newAddAttachmentList.add(punchListAttachment)
        punchListRejectReasonAttachments.add(punchListRejectReasonAttachment)
        Log.i("Punchlist", "onActivityResult: " + punchListRejectReasonAttachments.size)
        if (punchListRejectReasonAttachments.size >= 11 && punchListRejectReasonAttachments[0] == null) {
            punchListRejectReasonAttachments.removeAt(0)
        }
        rejectReasonAttachmentAdaptor.notifyDataSetChanged()
    }

    private fun saveFile(data: Uri? = null, captureData: Intent?) {
        var dateString: String? = ""
        try {
            val output = FileUtils.getOutputGalleryMediaFile(
                1,
                context
            )
            val thumbnail1 = if (captureData != null) {
                (captureData.extras as BaseBundle).get("data") as Bitmap
            } else {
                MediaStore.Images.Media.getBitmap(this.activity?.contentResolver, data)
            }

            try {
                val out = FileOutputStream(output)
                val thumbnail: Bitmap? = getRotateBitmap(output.absolutePath, thumbnail1)
                thumbnail?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                if (output.exists()) //Extra check, Just to validate the given path
                {
                    var intf: ExifInterface? = null
                    try {
                        intf = ExifInterface(output.absolutePath)
                        if (intf != null) {
                            dateString = intf.getAttribute(ExifInterface.TAG_DATETIME)
                            Log.i(
                                "Dated : ",
                                "DATE " + dateString.toString()
                            ) //Dispaly dateString. You can do/use it your own way
                        }
                    } catch (e: Exception) {
                    }
                    if (intf == null) {
                        val lastModDate = Date(output.lastModified())
                        Log.i(
                            "Dated : ",
                            "DATE $lastModDate"
                        ) //Dispaly lastModDate. You can do/use it your own way
                    }
                }
                try {
                    val captureIntent = Intent(
                        activity,
                        PronovosCameraActivity::class.java
                    ).putExtra("totalImageCount", 1).putExtra("file_location", output.path)
                    startActivityForResult(captureIntent, CAPTURE_IMAGE)
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun getRotateBitmap(photoPath: String?, bitmap: Bitmap?): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            var ei = androidx.exifinterface.media.ExifInterface(photoPath!!)
            val orientation = ei.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
            )
            rotatedBitmap =
                when (orientation) {
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> PunchlistFragment.rotateImage(
                        bitmap,
                        90f
                    )
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> PunchlistFragment.rotateImage(
                        bitmap,
                        180f
                    )
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> PunchlistFragment.rotateImage(
                        bitmap,
                        270f
                    )
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        }
        return rotatedBitmap ?: bitmap
    }

    companion object {
        const val TAG = "RejectPunchListDialog"
        const val CAPTURE_IMAGE = 2222
        const val PERMISSION_READ_REQUEST_CODE = 113
        const val TAKE_PICTURE = 3115
        const val SELECT_PICTURE = 5645
    }

}
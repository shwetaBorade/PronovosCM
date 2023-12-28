package com.pronovoscm.model.response.punchlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface

object ImageRotation {
   /* private fun handleRotation(photoBitmap: Bitmap): Bitmap {
        var photoBitmap1 = photoBitmap
        val filename = "tempPhotoFile"
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(photo!!.data)
        }

        val ei = ExifInterface(getFilesDir().path + "/" + filename)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> photoBitmap1 = rotateImage(photoBitmap1, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> photoBitmap1 = rotateImage(photoBitmap1, 180f)
        }

        return photoBitmap1
    }*/

    private fun rotateImage(bm: Bitmap, rotation: Float): Bitmap {
        if (rotation != 0f) {
            val matrix = Matrix()
            matrix.postRotate(rotation)
            return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        }
        return bm
    }
}
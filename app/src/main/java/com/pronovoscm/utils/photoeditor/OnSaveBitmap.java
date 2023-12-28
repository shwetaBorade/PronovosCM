package com.pronovoscm.utils.photoeditor;

import android.graphics.Bitmap;

/**
 * Created on 3/10/18.
 *
 * @author Sanjay Kushwah
 */
public interface OnSaveBitmap {
    void onBitmapReady(Bitmap saveBitmap);

    void onFailure(Exception e);
}

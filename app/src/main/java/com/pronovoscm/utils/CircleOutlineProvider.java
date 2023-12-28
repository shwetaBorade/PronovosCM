package com.pronovoscm.utils;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.pronovoscm.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CircleOutlineProvider extends ViewOutlineProvider {
    @Override
    public void getOutline(View view, Outline outline) {
        int cornerRadius = (int) (view.getContext().getResources().getDimension(R.dimen.album_photo_radius) / view.getContext().getResources().getDisplayMetrics().density);


        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {

                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 1);

            }
        });
    }
}
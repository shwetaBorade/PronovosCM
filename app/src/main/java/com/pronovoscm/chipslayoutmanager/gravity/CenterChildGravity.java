package com.pronovoscm.chipslayoutmanager.gravity;

import android.view.Gravity;

import com.pronovoscm.chipslayoutmanager.SpanLayoutChildGravity;

public class CenterChildGravity implements IChildGravityResolver {
    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return Gravity.CENTER_VERTICAL;
    }
}

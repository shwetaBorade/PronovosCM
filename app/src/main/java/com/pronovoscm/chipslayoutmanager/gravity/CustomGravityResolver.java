package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.SpanLayoutChildGravity;

public class CustomGravityResolver implements IChildGravityResolver {

    @SpanLayoutChildGravity
    private int gravity;

    public CustomGravityResolver(int gravity) {
        this.gravity = gravity;
    }

    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return gravity;
    }
}

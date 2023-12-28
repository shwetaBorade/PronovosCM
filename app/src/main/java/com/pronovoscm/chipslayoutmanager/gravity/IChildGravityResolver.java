package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.SpanLayoutChildGravity;

/** class which determines child gravity inside row from child position */
public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}

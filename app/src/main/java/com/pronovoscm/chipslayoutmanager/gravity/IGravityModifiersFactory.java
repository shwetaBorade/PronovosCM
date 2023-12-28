package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.SpanLayoutChildGravity;

public interface IGravityModifiersFactory {
    IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity);
}

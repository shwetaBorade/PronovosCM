package com.pronovoscm.chipslayoutmanager;

interface IStateHolder {
    boolean isLayoutRTL();

    @Orientation
    int layoutOrientation();

}

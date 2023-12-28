package com.pronovoscm.chipslayoutmanager.util;

import android.view.View;

import com.pronovoscm.chipslayoutmanager.layouter.IStateFactory;

public class StateHelper {
    public static boolean isInfinite(IStateFactory stateFactory) {
        return stateFactory.getSizeMode() == View.MeasureSpec.UNSPECIFIED
                && stateFactory.getEnd() == 0;
    }
}

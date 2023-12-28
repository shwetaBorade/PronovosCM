package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

public interface IFinishingCriteria {
    /** check if layouting finished by criteria */
    boolean isFinishedLayouting(AbstractLayouter abstractLayouter);
}

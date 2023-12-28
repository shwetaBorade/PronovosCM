package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

public class EmtpyCriteria implements IFinishingCriteria {
    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return true;
    }

}

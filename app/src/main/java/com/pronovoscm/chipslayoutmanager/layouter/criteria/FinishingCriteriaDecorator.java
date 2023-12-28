package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

abstract class FinishingCriteriaDecorator implements IFinishingCriteria {

    private IFinishingCriteria finishingCriteria;

    FinishingCriteriaDecorator(IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return finishingCriteria.isFinishedLayouting(abstractLayouter);
    }
}

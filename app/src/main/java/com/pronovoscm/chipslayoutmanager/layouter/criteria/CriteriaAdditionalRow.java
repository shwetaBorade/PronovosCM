package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;
import com.pronovoscm.chipslayoutmanager.layouter.ILayouter;
import com.pronovoscm.chipslayoutmanager.layouter.ILayouterListener;

class CriteriaAdditionalRow extends FinishingCriteriaDecorator implements IFinishingCriteria, ILayouterListener {

    private int requiredRowsCount;

    private int additionalRowsCount;

    CriteriaAdditionalRow(IFinishingCriteria finishingCriteria, int requiredRowsCount) {
        super(finishingCriteria);
        this.requiredRowsCount = requiredRowsCount;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        abstractLayouter.addLayouterListener(this);
        return super.isFinishedLayouting(abstractLayouter) && additionalRowsCount >= requiredRowsCount;
    }

    @Override
    public void onLayoutRow(ILayouter layouter) {
        if (super.isFinishedLayouting((AbstractLayouter) layouter)) {
            additionalRowsCount++;
        }
    }
}

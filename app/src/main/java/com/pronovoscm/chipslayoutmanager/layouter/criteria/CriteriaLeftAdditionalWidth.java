package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class CriteriaLeftAdditionalWidth extends FinishingCriteriaDecorator {

    private int additionalWidth;

    CriteriaLeftAdditionalWidth(IFinishingCriteria finishingCriteria, int additionalWidth) {
        super(finishingCriteria);
        this.additionalWidth = additionalWidth;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        int leftBorder = abstractLayouter.getCanvasLeftBorder();
        return super.isFinishedLayouting(abstractLayouter) &&
                //if additional height filled
                abstractLayouter.getViewRight() < leftBorder - additionalWidth;
    }

}

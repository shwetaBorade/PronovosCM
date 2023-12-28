package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class CriteriaLeftLayouterFinished implements IFinishingCriteria {
    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return abstractLayouter.getViewRight() <= abstractLayouter.getCanvasLeftBorder();
    }
}

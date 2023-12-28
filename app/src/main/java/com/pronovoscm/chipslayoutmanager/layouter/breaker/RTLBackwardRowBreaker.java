package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class RTLBackwardRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewLeft() + al.getCurrentViewWidth() > al.getCanvasRightBorder()
                && al.getViewLeft() > al.getCanvasLeftBorder();
    }
}

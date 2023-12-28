package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class RTLForwardRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewRight() < al.getCanvasRightBorder()
                && al.getViewRight() - al.getCurrentViewWidth() < al.getCanvasLeftBorder();

    }
}

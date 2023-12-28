package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class LTRBackwardRowBreaker implements ILayoutRowBreaker {
    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewRight() - al.getCurrentViewWidth() < al.getCanvasLeftBorder()
                && al.getViewRight() < al.getCanvasRightBorder();
    }
}

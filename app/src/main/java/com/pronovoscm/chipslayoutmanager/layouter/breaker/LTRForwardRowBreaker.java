package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

class LTRForwardRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewLeft() > al.getCanvasLeftBorder()
                && al.getViewLeft() + al.getCurrentViewWidth() > al.getCanvasRightBorder();
    }
}

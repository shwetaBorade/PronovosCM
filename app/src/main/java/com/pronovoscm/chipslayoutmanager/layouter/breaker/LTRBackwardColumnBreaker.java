package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

public class LTRBackwardColumnBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewBottom() - al.getCurrentViewHeight() < al.getCanvasTopBorder()
                && al.getViewBottom() < al.getCanvasBottomBorder();
    }
}

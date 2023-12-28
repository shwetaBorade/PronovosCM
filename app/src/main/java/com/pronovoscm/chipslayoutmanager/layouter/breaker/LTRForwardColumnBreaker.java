package com.pronovoscm.chipslayoutmanager.layouter.breaker;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;

public class LTRForwardColumnBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewTop() > al.getCanvasTopBorder()
                && al.getViewTop() + al.getCurrentViewHeight() > al.getCanvasBottomBorder();
    }
}

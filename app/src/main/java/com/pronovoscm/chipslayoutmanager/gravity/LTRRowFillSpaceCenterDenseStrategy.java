package com.pronovoscm.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;
import com.pronovoscm.chipslayoutmanager.layouter.Item;

import java.util.List;

class LTRRowFillSpaceCenterDenseStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter) / 2;

        for (Item item : row) {
            Rect childRect = item.getViewRect();
            childRect.left += difference;
            childRect.right += difference;
        }
    }
}

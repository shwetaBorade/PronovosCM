package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;
import com.pronovoscm.chipslayoutmanager.layouter.Item;

import java.util.List;

class EmptyRowStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        //do nothing
    }
}

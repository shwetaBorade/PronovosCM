package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;
import com.pronovoscm.chipslayoutmanager.layouter.Item;

import java.util.List;

public interface IRowStrategy {
    void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row);
}

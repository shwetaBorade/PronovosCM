package com.pronovoscm.chipslayoutmanager.gravity;

import androidx.annotation.NonNull;

import com.pronovoscm.chipslayoutmanager.layouter.AbstractLayouter;
import com.pronovoscm.chipslayoutmanager.layouter.Item;

import java.util.List;

class StrategyDecorator implements IRowStrategy {

    @NonNull
    private IRowStrategy rowStrategy;

    StrategyDecorator(@NonNull IRowStrategy rowStrategy) {
        this.rowStrategy = rowStrategy;
    }

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        rowStrategy.applyStrategy(abstractLayouter, row);
    }
}

package com.pronovoscm.chipslayoutmanager.layouter;

import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.IBreakerFactory;

interface IOrientationStateFactory {
    ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm);
    IRowStrategyFactory createRowStrategyFactory();
    IBreakerFactory createDefaultBreaker();
}

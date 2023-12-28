package com.pronovoscm.chipslayoutmanager.layouter;

import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.gravity.RTLRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.IBreakerFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;

class RTLRowsOrientationStateFactory implements IOrientationStateFactory {

    @Override
    public ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm) {
        return new RTLRowsCreator(lm);
    }

    @Override
    public IRowStrategyFactory createRowStrategyFactory() {
        return new RTLRowStrategyFactory();
    }

    @Override
    public IBreakerFactory createDefaultBreaker() {
        return new RTLRowBreakerFactory();
    }
}

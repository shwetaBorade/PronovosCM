package com.pronovoscm.chipslayoutmanager.layouter;

import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.gravity.LTRRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.IBreakerFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;

class LTRRowsOrientationStateFactory implements IOrientationStateFactory {

    @Override
    public ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm) {
        return new LTRRowsCreator(lm);
    }

    @Override
    public IRowStrategyFactory createRowStrategyFactory() {
        return new LTRRowStrategyFactory();
    }

    @Override
    public IBreakerFactory createDefaultBreaker() {
        return new LTRRowBreakerFactory();
    }
}

package com.pronovoscm.chipslayoutmanager.layouter;

import android.view.View;

import com.pronovoscm.chipslayoutmanager.ChipsLayoutManager;
import com.pronovoscm.chipslayoutmanager.IScrollingController;
import com.pronovoscm.chipslayoutmanager.anchor.AnchorViewState;
import com.pronovoscm.chipslayoutmanager.anchor.ColumnsAnchorFactory;
import com.pronovoscm.chipslayoutmanager.anchor.IAnchorFactory;
import com.pronovoscm.chipslayoutmanager.cache.IViewCacheStorage;
import com.pronovoscm.chipslayoutmanager.gravity.ColumnGravityModifiersFactory;
import com.pronovoscm.chipslayoutmanager.gravity.ColumnStrategyFactory;
import com.pronovoscm.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.ColumnBreakerFactory;
import com.pronovoscm.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.pronovoscm.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.pronovoscm.chipslayoutmanager.layouter.criteria.ColumnsCriteriaFactory;
import com.pronovoscm.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.pronovoscm.chipslayoutmanager.layouter.criteria.InfiniteCriteriaFactory;
import com.pronovoscm.chipslayoutmanager.layouter.placer.IPlacerFactory;
import com.pronovoscm.chipslayoutmanager.util.StateHelper;

public class ColumnsStateFactory implements IStateFactory {

    private ChipsLayoutManager lm;
    private IRowStrategyFactory rowStrategyFactory;

    public ColumnsStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
        rowStrategyFactory = new ColumnStrategyFactory();
    }

    @Override
    public LayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        IViewCacheStorage cacheStorage = lm.getViewPositionsStorage();

        return createColumnLayouterFactory(criteriaFactory, placerFactory, cacheStorage);
    }

    private LayouterFactory createColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
         return new LayouterFactory(lm,
                 new ColumnsCreator(lm),
                 new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new ColumnBreakerFactory()),
                 criteriaFactory,
                 placerFactory,
                 new ColumnGravityModifiersFactory(),
                 rowStrategyFactory.createRowStrategy(lm.getRowStrategyType()));
    }

    @Override
    public AbstractCriteriaFactory createDefaultFinishingCriteriaFactory() {
        return StateHelper.isInfinite(this)? new InfiniteCriteriaFactory() : new ColumnsCriteriaFactory();
    }

    @Override
    public IAnchorFactory anchorFactory() {
        return new ColumnsAnchorFactory(lm, lm.getCanvas());
    }

    @Override
    public IScrollingController scrollingController() {
        return lm.horizontalScrollingController();
    }

    @Override
    public ICanvas createCanvas() {
        return new ColumnSquare(lm);
    }

    @Override
    public int getSizeMode() {
        return lm.getWidthMode();
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public int getStart(View view) {
        return lm.getDecoratedLeft(view);
    }

    @Override
    public int getStart(AnchorViewState anchor) {
        return anchor.getAnchorViewRect().left;
    }

    @Override
    public int getEnd() {
        return lm.getWidth();
    }

    @Override
    public int getEnd(View view) {
        return lm.getDecoratedRight(view);
    }

    @Override
    public int getEnd(AnchorViewState anchor) {
        return anchor.getAnchorViewRect().right;
    }

    @Override
    public int getEndViewPosition() {
        return lm.getPosition(lm.getCanvas().getBottomView());
    }

    @Override
    public int getStartAfterPadding() {
        return lm.getPaddingLeft();
    }

    @Override
    public int getStartViewPosition() {
        return lm.getPosition(lm.getCanvas().getTopView());
    }

    @Override
    public int getEndAfterPadding() {
        return lm.getWidth() - lm.getPaddingRight();
    }

    @Override
    public int getStartViewBound() {
        return getStart(lm.getCanvas().getLeftView());
    }

    @Override
    public int getEndViewBound() {
        return getEnd(lm.getCanvas().getRightView());
    }

    @Override
    public int getTotalSpace() {
        return lm.getWidth() - lm.getPaddingLeft()
                - lm.getPaddingRight();
    }


}

package com.beloo.widget.chipslayoutmanager.layouter;

import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.HorizontalScrollingController;
import com.beloo.widget.chipslayoutmanager.IScrollingController;
import com.beloo.widget.chipslayoutmanager.anchor.ColumnsAnchorFactory;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.ColumnGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRColumnBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ColumnsCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

public class ColumnsStateFactory implements IStateFactory {

    private ChipsLayoutManager lm;

    public ColumnsStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    @Override
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        IViewCacheStorage cacheStorage = lm.getViewPositionsStorage();

        return createColumnLayouterFactory(criteriaFactory, placerFactory, cacheStorage);
    }

    private AbstractLayouterFactory createColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new LTRColumnsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRColumnBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new ColumnGravityModifiersFactory());
    }

    @Override
    public AbstractCriteriaFactory createDefaultFinishingCriteriaFactory() {
        return new ColumnsCriteriaFactory();
    }

    @Override
    public IAnchorFactory anchorFactory() {
        return new ColumnsAnchorFactory(lm, new Square(lm));
    }

    @Override
    public IScrollingController scrollingController() {
        return new HorizontalScrollingController(lm);
    }

    @Override
    public int getStart(View view) {
        return lm.getDecoratedLeft(view);
    }

    @Override
    public int getEnd(View view) {
        return lm.getDecoratedRight(view);
    }
}

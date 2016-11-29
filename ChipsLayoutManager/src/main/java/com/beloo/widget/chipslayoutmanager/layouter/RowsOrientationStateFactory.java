package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.RowGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

class RowsOrientationStateFactory implements IOrientationStateFactory {

    private ChipsLayoutManager lm;

    RowsOrientationStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    @Override
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        return lm.isLayoutRTL() ?
                createRTLRowLayouterFactory(criteriaFactory, placerFactory, lm.getViewPositionsStorage())
                : createLTRRowLayouterFactory(criteriaFactory, placerFactory, lm.getViewPositionsStorage());
    }

    private AbstractLayouterFactory createLTRRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new LTRRowsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRRowBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new RowGravityModifiersFactory());
    }

    private AbstractLayouterFactory createRTLRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new RTLRowsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new RTLRowBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new RowGravityModifiersFactory());
    }
}

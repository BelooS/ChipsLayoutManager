package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.gravity.RTLRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.gravity.RowGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

class RTLRowsOrientationStateFactory implements IOrientationStateFactory {

    private ChipsLayoutManager lm;
    private IRowStrategyFactory rowStrategyFactory;

    RTLRowsOrientationStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
        rowStrategyFactory = new RTLRowStrategyFactory();
    }

    @Override
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        return createRTLRowLayouterFactory(criteriaFactory, placerFactory, lm.getViewPositionsStorage());
    }

    private AbstractLayouterFactory createRTLRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new RTLRowsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new RTLRowBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new RowGravityModifiersFactory(),
                rowStrategyFactory.createRowStrategy(lm.getRowStrategy()));
    }
}

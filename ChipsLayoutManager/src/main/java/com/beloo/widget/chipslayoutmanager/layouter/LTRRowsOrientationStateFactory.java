package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.gravity.LTRRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.gravity.RowGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

class LTRRowsOrientationStateFactory implements IOrientationStateFactory {

    private ChipsLayoutManager lm;

    private IRowStrategyFactory rowStrategyFactory;

    LTRRowsOrientationStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
        rowStrategyFactory = new LTRRowStrategyFactory();
    }

    @Override
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        return createLTRRowLayouterFactory(criteriaFactory, placerFactory, lm.getViewPositionsStorage());
    }

    private AbstractLayouterFactory createLTRRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new LTRRowsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRRowBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new RowGravityModifiersFactory(),
                rowStrategyFactory.createRowStrategy(lm.getRowStrategy()));
    }
}

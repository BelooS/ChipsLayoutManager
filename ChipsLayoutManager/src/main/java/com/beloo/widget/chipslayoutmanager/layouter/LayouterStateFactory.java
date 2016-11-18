package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

public class LayouterStateFactory {

    private ChipsLayoutManager lm;

    public LayouterStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        IViewCacheStorage cacheStorage = lm.getViewPositionsStorage();

        AbstractLayouterFactory layouterFactory = lm.isLayoutRTL() ?
                createRTLRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage) : createLTRRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage);

        return layouterFactory;
    }

    private AbstractLayouterFactory createLTRRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new LTRLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRRowBreakerFactory()),
                criteriaFactory,
                placerFactory);
    }

    private AbstractLayouterFactory createRTLRowLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new RTLLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new RTLRowBreakerFactory()),
                criteriaFactory,
                placerFactory);
    }

}

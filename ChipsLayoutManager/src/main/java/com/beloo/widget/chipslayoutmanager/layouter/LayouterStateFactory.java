package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.IStateHolder;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.ColumnGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.gravity.RowGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRColumnBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ColumnsCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.RowsCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

public class LayouterStateFactory {

    private ChipsLayoutManager lm;

    public LayouterStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        IViewCacheStorage cacheStorage = lm.getViewPositionsStorage();

        AbstractLayouterFactory layouterFactory;

        if (lm.orientation() == IStateHolder.ROWS) {
            layouterFactory = lm.isLayoutRTL() ?
                    createRTLRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage) : createLTRRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage);
        } else {
            layouterFactory = createLTRColumnLayouterFactory(criteriaFactory, placerFactory, cacheStorage);
        }

        return layouterFactory;
    }

    public AbstractCriteriaFactory createDefaultFinishingCriteriaFactory() {
        if (lm.orientation() == IStateHolder.ROWS) {
            return new RowsCriteriaFactory();
        } else {
            return new ColumnsCriteriaFactory();
        }
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

    private AbstractLayouterFactory createLTRColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
        return new LTRColumnsLayouterFactory(lm, cacheStorage,
                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRColumnBreakerFactory()),
                criteriaFactory,
                placerFactory,
                new ColumnGravityModifiersFactory());
    }

//    private AbstractLayouterFactory createRTLColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
//        return new RTLRowsLayouterFactory(lm, cacheStorage,
//                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRColumnBreakerFactory()),
//                criteriaFactory,
//                placerFactory,
//                new ColumnGravityModifiersFactory());
//    }

}

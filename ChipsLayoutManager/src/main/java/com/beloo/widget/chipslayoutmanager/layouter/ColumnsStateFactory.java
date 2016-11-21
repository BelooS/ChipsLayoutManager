package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
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

        return createLTRColumnLayouterFactory(criteriaFactory, placerFactory, cacheStorage);
    }

    private AbstractLayouterFactory createLTRColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
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
    public IAnchorFactory createAnchorFactory() {
        return new ColumnsAnchorFactory(lm, new Square(lm));
    }

    //    private AbstractLayouterFactory createRTLColumnLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory, IViewCacheStorage cacheStorage) {
//        return new RTLRowsLayouterFactory(lm, cacheStorage,
//                new DecoratorBreakerFactory(cacheStorage, lm.getRowBreaker(), lm.getMaxViewsInRow(), new LTRColumnBreakerFactory()),
//                criteriaFactory,
//                placerFactory,
//                new ColumnGravityModifiersFactory());
//    }
}

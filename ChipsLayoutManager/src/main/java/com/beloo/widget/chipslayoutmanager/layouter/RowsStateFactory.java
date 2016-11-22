package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.IScrollingController;
import com.beloo.widget.chipslayoutmanager.VerticalScrollingController;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.anchor.RowsAnchorFactory;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.RowGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.DecoratorBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.LTRRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.RTLRowBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.RowsCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

public class RowsStateFactory implements IStateFactory {

    private ChipsLayoutManager lm;

    public RowsStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        IViewCacheStorage cacheStorage = lm.getViewPositionsStorage();

        AbstractLayouterFactory layouterFactory;

        layouterFactory = lm.isLayoutRTL() ?
                createRTLRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage) : createLTRRowLayouterFactory(criteriaFactory, placerFactory, cacheStorage);

        return layouterFactory;
    }

    @Override
    public AbstractCriteriaFactory createDefaultFinishingCriteriaFactory() {
        return new RowsCriteriaFactory();
    }

    @Override
    public IAnchorFactory anchorFactory() {
        return new RowsAnchorFactory(lm, new Square(lm));
    }

    @Override
    public IMeasureSupporter measureSupporter() {
        return new VerticalMeasureSupporter(lm);
    }

    @Override
    public IScrollingController scrollingController() {
        return new VerticalScrollingController(lm);
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

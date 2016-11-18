package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IBreakerFactory;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLayouterFactory {
    ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;

    private List<ILayouterListener> layouterListeners = new ArrayList<>();

    private IBreakerFactory breakerFactory;
    private ICriteriaFactory criteriaFactory;
    private IPlacerFactory placerFactory;
    private IGravityModifiersFactory gravityModifiersFactory;

    AbstractLayouterFactory(ChipsLayoutManager layoutManager,
                            IViewCacheStorage cacheStorage,
                            IBreakerFactory breakerFactory,
                            ICriteriaFactory criteriaFactory,
                            IPlacerFactory placerFactory,
                            IGravityModifiersFactory gravityModifiersFactory) {
        this.cacheStorage = cacheStorage;
        this.layoutManager = layoutManager;
        this.breakerFactory = breakerFactory;
        this.criteriaFactory = criteriaFactory;
        this.placerFactory = placerFactory;
        this.gravityModifiersFactory = gravityModifiersFactory;
    }

    public void addLayouterListener(@Nullable ILayouterListener layouterListener) {
        if (layouterListener != null) {
            layouterListeners.add(layouterListener);
        }
    }

    abstract AbstractLayouter.Builder createBackwardBuilder();
    abstract AbstractLayouter.Builder createForwardBuilder();
    abstract Rect createOffsetRectForBackwardLayouter(Rect anchorRect);
    abstract Rect createOffsetRectForForwardLayouter(Rect anchorRect);

    @NonNull
    private AbstractLayouter.Builder fillBasicBuilder(AbstractLayouter.Builder builder) {
        return builder.layoutManager(layoutManager)
                .canvas(new Square(layoutManager))
                .childGravityResolver(layoutManager.getChildGravityResolver())
                .cacheStorage(cacheStorage)
                .gravityModifiersFactory(gravityModifiersFactory)
                .addLayouterListeners(layouterListeners);
    }

    @NonNull
    public final ILayouter getBackwardLayouter(@Nullable Rect anchorRect) {
        return fillBasicBuilder(createBackwardBuilder())
                .offsetRect(createOffsetRectForBackwardLayouter(anchorRect))
                .breaker(breakerFactory.createBackwardRowBreaker())
                .finishingCriteria(criteriaFactory.getBackwardFinishingCriteria())
                .placer(placerFactory.getAtStartPlacer())
                .build();
    }

    @NonNull
    public final ILayouter getForwardLayouter(@Nullable Rect anchorRect) {
        return fillBasicBuilder(createForwardBuilder())
                .offsetRect(createOffsetRectForForwardLayouter(anchorRect))
                .breaker(breakerFactory.createForwardRowBreaker())
                .finishingCriteria(criteriaFactory.getForwardFinishingCriteria())
                .placer(placerFactory.getAtEndPlacer())
                .build();
    }

    @NonNull
    public final ILayouter buildForwardLayouter(@NonNull ILayouter layouter) {
        AbstractLayouter abstractLayouter = (AbstractLayouter) layouter;
        abstractLayouter.setFinishingCriteria(criteriaFactory.getForwardFinishingCriteria());
        abstractLayouter.setPlacer(placerFactory.getAtEndPlacer());

        return abstractLayouter;
    }

    @NonNull
    public final ILayouter buildBackwardLayouter(@NonNull ILayouter layouter) {
        AbstractLayouter abstractLayouter = (AbstractLayouter) layouter;
        abstractLayouter.setFinishingCriteria(criteriaFactory.getBackwardFinishingCriteria());
        abstractLayouter.setPlacer(placerFactory.getAtEndPlacer());

        return abstractLayouter;
    }
}

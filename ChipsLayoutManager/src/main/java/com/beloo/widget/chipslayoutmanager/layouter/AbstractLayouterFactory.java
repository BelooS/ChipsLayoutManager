package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaDownAdditionalHeight;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaDownLayouterFinished;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaUpLayouterFinished;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.InfiniteCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacer;
import com.beloo.widget.chipslayoutmanager.layouter.placer.RealBottomPlacer;
import com.beloo.widget.chipslayoutmanager.layouter.placer.RealTopPlacer;

public abstract class AbstractLayouterFactory {
    ChipsLayoutManager layoutManager;
    IViewCacheStorage cacheStorage;
    @Nullable
    private Integer maxViewsInRow = null;

    @IntRange(from = 0)
    private int additionalRowsCount;

    @Nullable
    private ILayouterListener layouterListener;

    private int additionalHeight;

    @NonNull
    private IPlacer bottomPlacer;
    @NonNull
    private IPlacer topPlacer;


    AbstractLayouterFactory(IViewCacheStorage cacheStorage, ChipsLayoutManager layoutManager) {
        this.cacheStorage = cacheStorage;
        this.layoutManager = layoutManager;
        bottomPlacer = new RealBottomPlacer(layoutManager);
        topPlacer = new RealTopPlacer(layoutManager);
    }

    public void setLayouterListener(@Nullable ILayouterListener layouterListener) {
        this.layouterListener = layouterListener;
    }

    public void setMaxViewsInRow(@Nullable Integer maxViewsInRow) {
        this.maxViewsInRow = maxViewsInRow;
    }

    public void setAdditionalHeight(@IntRange(from = 0) int additionalHeight) {
        if (additionalHeight < 0) throw new IllegalArgumentException("additional height can't be negative");
        this.additionalHeight = additionalHeight;
    }

    public void setAdditionalRowsCount(int additionalRowsCount) {
        this.additionalRowsCount = additionalRowsCount;
    }

    //todo move to criteria factory
    @NonNull
    IFinishingCriteria getUpFinishingCriteria() {
        return new CriteriaUpLayouterFinished();
    }

    @NonNull
    IFinishingCriteria getDownFinishingCriteria() {
        return new CriteriaDownAdditionalHeight(new CriteriaDownLayouterFinished(), getAdditionalHeight());
    }

    @NonNull
    IPlacer getBottomPlacer() {
        return bottomPlacer;
    }

    @NonNull
    IPlacer getTopPlacer() {
        return topPlacer;
    }

    int getAdditionalRowsCount() {
        return additionalRowsCount;
    }

    @Nullable
    Integer getMaxViewsInRow() {
        return maxViewsInRow;
    }

    @Nullable
    ILayouterListener getLayouterListener() {
        return layouterListener;
    }

    public int getAdditionalHeight() {
        return additionalHeight;
    }

    public abstract ILayouter getUpLayouter(@Nullable Rect anchorRect);
    public abstract ILayouter getDownLayouter(@Nullable Rect anchorRect);
    public abstract ILayouter getDisappearingDownLayouter(@Nullable Rect anchorRect);

    public ILayouter createInfiniteLayouter(ILayouter layouter) {
        ((AbstractLayouter)layouter).setFinishingCriteria(new InfiniteCriteria());
        return layouter;
    }
}

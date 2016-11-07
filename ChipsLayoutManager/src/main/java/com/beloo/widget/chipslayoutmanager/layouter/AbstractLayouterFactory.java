package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaAdditionalRow;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaDownAdditionalHeight;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaDownLayouterFinished;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaUpLayouterFinished;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.EmtpyCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.InfiniteCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.DisappearingViewBottomPlacer;
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

    AbstractLayouterFactory(IViewCacheStorage cacheStorage, ChipsLayoutManager layoutManager) {
        this.cacheStorage = cacheStorage;
        this.layoutManager = layoutManager;
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

    int getAdditionalHeight() {
        return additionalHeight;
    }

    abstract AbstractLayouter.Builder createUpBuilder(Rect anchorRect);
    abstract AbstractLayouter.Builder createDownBuilder(Rect anchorRect);

    private AbstractLayouter.Builder fillBasicBuilder(AbstractLayouter.Builder builder) {
        return builder.layoutManager(layoutManager)
                .canvas(new Square(layoutManager))
                .childGravityResolver(layoutManager.getChildGravityResolver())
                .cacheStorage(cacheStorage)
                .maxCountInRow(getMaxViewsInRow())
                .addLayouterListener(getLayouterListener());
    }

    public final ILayouter getUpLayouter(@Nullable Rect anchorRect) {
        return fillBasicBuilder(createUpBuilder(anchorRect))
                .finishingCriteria(getUpFinishingCriteria())
                .placer(new RealTopPlacer(layoutManager))
                .build();
    }

    public final ILayouter getDownLayouter(@Nullable Rect anchorRect) {
        return fillBasicBuilder(createDownBuilder(anchorRect))
                .finishingCriteria(getDownFinishingCriteria())
                .placer(new RealBottomPlacer(layoutManager))
                .build();
    }

    public final ILayouter getDisappearingDownLayouter(@Nullable Rect anchorRect) {
        return fillBasicBuilder(createDownBuilder(anchorRect))
                .finishingCriteria(new CriteriaAdditionalRow(new EmtpyCriteria(), getAdditionalRowsCount()))
                .placer(new DisappearingViewBottomPlacer(layoutManager))
                .build();
    }

    public ILayouter createInfiniteLayouter(ILayouter layouter) {
        ((AbstractLayouter)layouter).setFinishingCriteria(new InfiniteCriteria());
        return layouter;
    }
}

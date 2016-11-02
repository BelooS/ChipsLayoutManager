package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

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
}

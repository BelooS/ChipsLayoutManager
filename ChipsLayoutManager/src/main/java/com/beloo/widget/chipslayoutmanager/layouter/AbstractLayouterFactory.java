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

    AbstractLayouterFactory(IViewCacheStorage cacheStorage, ChipsLayoutManager layoutManager) {
        this.cacheStorage = cacheStorage;
        this.layoutManager = layoutManager;
    }

    public void setMaxViewsInRow(@Nullable Integer maxViewsInRow) {
        this.maxViewsInRow = maxViewsInRow;
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

    public abstract ILayouter getUpLayouter(@Nullable Rect anchorRect);
    public abstract ILayouter getDownLayouter(@Nullable Rect anchorRect);
}

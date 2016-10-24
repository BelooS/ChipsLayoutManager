package com.beloo.widget.spanlayoutmanager.layouter;

import android.support.annotation.Nullable;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public abstract class AbstractLayouterFactory {
    ChipsLayoutManager layoutManager;
    IViewCacheStorage cacheStorage;
    @Nullable
    private Integer maxViewsInRow = null;

    AbstractLayouterFactory(IViewCacheStorage cacheStorage, ChipsLayoutManager layoutManager) {
        this.cacheStorage = cacheStorage;
        this.layoutManager = layoutManager;
    }

    public void setMaxViewsInRow(@Nullable Integer maxViewsInRow) {
        this.maxViewsInRow = maxViewsInRow;
    }

    @Nullable
    public Integer getMaxViewsInRow() {
        return maxViewsInRow;
    }

    public abstract ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight);
    public abstract ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight);
}

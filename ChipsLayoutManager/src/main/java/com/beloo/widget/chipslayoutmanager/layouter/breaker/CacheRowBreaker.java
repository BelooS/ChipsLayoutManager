package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class CacheRowBreaker extends RowBreakerDecorator {

    private IViewCacheStorage cacheStorage;

    CacheRowBreaker(ILayoutRowBreaker decorate, IViewCacheStorage cacheStorage) {
        super(decorate);
        this.cacheStorage = cacheStorage;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        boolean stopDueToCache = cacheStorage.isPositionEndsRow(al.getCurrentViewPosition());
        return super.isRowBroke(al) || stopDueToCache;
    }
}

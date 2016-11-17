package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

public class DecoratorBreakerFactory implements IBreakerFactory {

    private IBreakerFactory breakerFactory;
    private IViewCacheStorage cacheStorage;
    private IRowBreaker rowBreaker;

    /** Max items in row restriction. Layout of row should be stopped when this count of views reached*/
    @Nullable
    private Integer maxViewsInRow;

    public DecoratorBreakerFactory(@NonNull IViewCacheStorage cacheStorage,
                                   @NonNull IRowBreaker rowBreaker,
                                   @Nullable Integer maxViewsInRow,
                                   @NonNull IBreakerFactory breakerFactory) {
        this.cacheStorage = cacheStorage;
        this.rowBreaker = rowBreaker;
        this.maxViewsInRow = maxViewsInRow;
        this.breakerFactory = breakerFactory;
    }

    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createBackwardRowBreaker();
        breaker = new BackwardBreakerContract(rowBreaker, new CacheRowBreaker(cacheStorage, breaker));
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createForwardRowBreaker();
        breaker = new ForwardBreakerContract(rowBreaker, breaker);
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }
}

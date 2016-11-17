package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

public class DecoratorBreakerFactory implements IBreakerFactory {

    private IBreakerFactory breakerFactory;
    private IViewCacheStorage cacheStorage;
    private IRowBreaker rowBreaker;

    @Nullable
    private Integer maxViewsInRow;

    public DecoratorBreakerFactory(IBreakerFactory breakerFactory, IViewCacheStorage cacheStorage, IRowBreaker rowBreaker, @Nullable Integer maxViewsInRow) {
        this.breakerFactory = breakerFactory;
        this.cacheStorage = cacheStorage;
        this.rowBreaker = rowBreaker;
        this.maxViewsInRow = maxViewsInRow;
    }
    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createBackwardRowBreaker();
        breaker = new BackwardRowBreakerContract(rowBreaker, new CacheRowBreaker(cacheStorage, breaker));
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createForwardRowBreaker();
        breaker = new ForwardRowBreakerContract(rowBreaker, breaker);
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }
}

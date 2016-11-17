package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

public class DecoratorBreakerFactory implements IBreakerFactory {

    private IBreakerFactory breakerFactory;
    private IViewCacheStorage cacheStorage;
    private IRowBreaker rowBreaker;

    /** Max items in row restriction. Layout of row should be stopped when this count of views reached*/
    @Nullable
    private Integer maxViewsInRow;

    public DecoratorBreakerFactory(IViewCacheStorage cacheStorage, IRowBreaker rowBreaker, @Nullable Integer maxViewsInRow) {
        this.cacheStorage = cacheStorage;
        this.rowBreaker = rowBreaker;
        this.maxViewsInRow = maxViewsInRow;
    }

    public void withBreakerFactory(IBreakerFactory breakerFactory) {
        this.breakerFactory = breakerFactory;
    }

    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        if (breakerFactory == null) throw new IllegalStateException("breaker factory should be initialized with #withBreakerFactory");
        ILayoutRowBreaker breaker = breakerFactory.createBackwardRowBreaker();
        breaker = new BackwardRowBreakerContract(rowBreaker, new CacheRowBreaker(cacheStorage, breaker));
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        if (breakerFactory == null) throw new IllegalStateException("breaker factory should be initialized with #withBreakerFactory");
        ILayoutRowBreaker breaker = breakerFactory.createForwardRowBreaker();
        breaker = new ForwardRowBreakerContract(rowBreaker, breaker);
        if (maxViewsInRow != null) {
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        }
        return breaker;
    }
}

package com.beloo.widget.chipslayoutmanager.layouter.breaker;

public class RTLBreakerFactory implements IBreakerFactory {
    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        return new RTLUpRowBreaker();
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        return new RTLDownRowBreaker();
    }
}

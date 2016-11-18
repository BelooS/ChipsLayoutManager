package com.beloo.widget.chipslayoutmanager.layouter.breaker;

public class LTRColumnBreakerFactory implements IBreakerFactory {
    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        return new BackwardColumnBreaker();
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        return new ForwardColumnBreaker();
    }
}

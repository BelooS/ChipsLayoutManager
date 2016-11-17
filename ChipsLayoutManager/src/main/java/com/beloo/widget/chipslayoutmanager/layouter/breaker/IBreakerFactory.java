package com.beloo.widget.chipslayoutmanager.layouter.breaker;

public interface IBreakerFactory {
    ILayoutRowBreaker createBackwardRowBreaker();

    ILayoutRowBreaker createForwardRowBreaker();
}

package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class BackwardRowBreakerContract extends RowBreakerDecorator{

    private IRowBreaker breaker;

    public BackwardRowBreakerContract(ILayoutRowBreaker decorate, IRowBreaker breaker) {
        super(decorate);
        this.breaker = breaker;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return super.isRowBroke(al) ||
                breaker.isItemBreakRow(al.getCurrentViewPosition());
    }
}

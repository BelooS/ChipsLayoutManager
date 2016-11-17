package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class BackwardRowBreakerContract extends RowBreakerDecorator{

    private IRowBreaker breaker;

    BackwardRowBreakerContract(IRowBreaker breaker, ILayoutRowBreaker decorate) {
        super(decorate);
        this.breaker = breaker;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return super.isRowBroke(al) ||
                breaker.isItemBreakRow(al.getCurrentViewPosition());
    }
}

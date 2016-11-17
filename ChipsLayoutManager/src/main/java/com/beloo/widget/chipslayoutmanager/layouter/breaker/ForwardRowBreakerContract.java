package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class ForwardRowBreakerContract extends RowBreakerDecorator {

    private IRowBreaker breaker;

    ForwardRowBreakerContract(IRowBreaker breaker, ILayoutRowBreaker decorate) {
        super(decorate);
        this.breaker = breaker;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return super.isRowBroke(al) ||
                (al.getCurrentViewPosition() != 0 && breaker.isItemBreakRow(al.getCurrentViewPosition() - 1));
    }
}

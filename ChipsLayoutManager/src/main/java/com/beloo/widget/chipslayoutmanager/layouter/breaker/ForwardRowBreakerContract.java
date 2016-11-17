package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class ForwardRowBreakerContract extends RowBreakerDecorator {

    private IRowBreaker breaker;

    ForwardRowBreakerContract(ILayoutRowBreaker decorate, IRowBreaker breaker) {
        super(decorate);
        this.breaker = breaker;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return super.isRowBroke(al) ||
                (al.getCurrentViewPosition() != 0 && breaker.isItemBreakRow(al.getCurrentViewPosition() - 1));
    }
}

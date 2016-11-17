package com.beloo.widget.chipslayoutmanager.layouter.breaker;

public class EmptyRowBreaker implements IRowBreaker {
    @Override
    public boolean isItemBreakRow(int position) {
        return false;
    }
}

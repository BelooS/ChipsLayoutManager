package com.beloo.widget.chipslayoutmanager.breaker;

public class EmptyRowBreaker implements IRowBreaker {
    @Override
    public boolean isItemBreakRow(int position) {
        return false;
    }
}

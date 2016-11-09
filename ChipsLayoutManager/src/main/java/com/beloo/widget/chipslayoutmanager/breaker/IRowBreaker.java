package com.beloo.widget.chipslayoutmanager.breaker;

/** determines whether LM should break row from view position  */
public interface IRowBreaker {
    boolean isItemBreakRow(int position);
}

package com.beloo.widget.chipslayoutmanager.breaker;

import android.support.annotation.IntRange;

/** determines whether LM should break row from view position  */
public interface IRowBreaker {
    boolean isItemBreakRow(@IntRange(from = 0) int position);
}

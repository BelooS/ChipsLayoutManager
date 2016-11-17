package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import android.support.annotation.IntRange;

/** determines whether LM should break row from view position  */
public interface IRowBreaker {
    /** @return `true` means that it is the last view in the row.
     * `false` means that breaking behaviour about current view will be based on another conditions  */
    boolean isItemBreakRow(@IntRange(from = 0) int position);
}

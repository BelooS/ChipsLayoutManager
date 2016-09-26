package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import android.support.annotation.IntRange;

public class PositionIteratorFactory {
    /** creates incremental position iterator. Increments by 1 on each next calling
     * @param itemCount maxValue of increment. */
    public AbstractPositionIterator getIncrementalPositionIterator(@IntRange(from = 0) int itemCount){
        if (itemCount < 0) throw new IllegalArgumentException("item count can't be negative");
        return new IncrementalPositionIterator(itemCount);
    }

    /** creates decremental position iterator. Zero is min value*/
    public AbstractPositionIterator getDecrementalPositionIterator() {
        return new DecrementalPositionIterator();
    }

}

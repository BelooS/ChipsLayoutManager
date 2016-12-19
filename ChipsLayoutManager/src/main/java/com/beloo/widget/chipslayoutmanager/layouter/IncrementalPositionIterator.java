package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.annotation.IntRange;

class IncrementalPositionIterator extends AbstractPositionIterator {

    IncrementalPositionIterator(@IntRange(from = 0) int itemCount) {
        super(itemCount);
    }

    @Override
    public boolean hasNext() {
        return pos < itemCount;
    }

    @Override
    public Integer next() {
        if (!hasNext()) throw new IllegalStateException("position out of bounds reached");
        return pos++;
    }

}

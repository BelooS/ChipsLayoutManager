package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.annotation.IntRange;

class DecrementalPositionIterator extends AbstractPositionIterator {

    DecrementalPositionIterator(@IntRange(from = 0) int itemCount) {
        super(itemCount);
    }

    @Override
    public boolean hasNext() {
        return pos >= 0;
    }

    @Override
    public Integer next() {
        if (!hasNext()) throw new IllegalStateException("position out of bounds reached");
        return pos--;
    }

}

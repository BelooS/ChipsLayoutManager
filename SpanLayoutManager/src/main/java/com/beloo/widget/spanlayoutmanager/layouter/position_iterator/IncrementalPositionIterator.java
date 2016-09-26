package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import android.support.annotation.IntRange;

class IncrementalPositionIterator extends AbstractPositionIterator {

    private int itemCount;

    IncrementalPositionIterator(@IntRange(from = 0) int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public void move(@IntRange(from = 0) int pos) {
        if (pos >= itemCount) throw new IllegalArgumentException("can't move above items count");
        super.move(pos);
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

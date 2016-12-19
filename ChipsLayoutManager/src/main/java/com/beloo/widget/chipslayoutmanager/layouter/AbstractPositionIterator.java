package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.annotation.IntRange;

import java.util.Iterator;

public abstract class AbstractPositionIterator implements Iterator<Integer> {
    int pos;
    int itemCount;

    AbstractPositionIterator(@IntRange(from = 0) int itemCount) {
        if (itemCount < 0) throw new IllegalArgumentException("item count couldn't be negative");
        this.itemCount = itemCount;
    }

    public void move(@IntRange(from = 0) int pos) {
        if (pos >= itemCount) throw new IllegalArgumentException("you can't move above of maxItemCount");
        if (pos < 0) throw new IllegalArgumentException("can't move to negative position");
        this.pos = pos;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("removing not supported in position iterator");
    }
}

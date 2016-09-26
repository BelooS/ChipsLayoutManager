package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import android.support.annotation.IntRange;

import java.util.Iterator;

public abstract class AbstractPositionIterator implements Iterator<Integer> {
    protected int pos;

    public void move(@IntRange(from = 0) int pos) {
        if (pos < 0) throw new IllegalArgumentException("can't move to negative position");
        this.pos = pos;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("removing not supported in position iterator");
    }
}

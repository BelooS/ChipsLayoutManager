package com.beloo.widget.chipslayoutmanager.layouter;

import java.util.Iterator;

public abstract class AbstractPositionIterator implements Iterator<Integer> {
    protected int pos;

    public void move(int pos) {
        this.pos = pos;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("removing not supported in position iterator");
    }
}

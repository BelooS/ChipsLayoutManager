package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

class DecrementalPositionIterator extends AbstractPositionIterator {

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

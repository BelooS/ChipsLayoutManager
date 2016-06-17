package com.beloo.widget.spanlayoutmanager.layouter;

class IncrementalPositionIterator extends AbstractPositionIterator {

    private int itemCount;

    public IncrementalPositionIterator(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public boolean hasNext() {
        return pos < itemCount;
    }

    @Override
    public Integer next() {
        return pos++;
    }

}

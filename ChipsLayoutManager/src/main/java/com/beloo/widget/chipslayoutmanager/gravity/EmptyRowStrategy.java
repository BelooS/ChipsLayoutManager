package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class EmptyRowStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        //do nothing
    }
}

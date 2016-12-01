package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

public interface IRowStrategy {
    void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row);
}

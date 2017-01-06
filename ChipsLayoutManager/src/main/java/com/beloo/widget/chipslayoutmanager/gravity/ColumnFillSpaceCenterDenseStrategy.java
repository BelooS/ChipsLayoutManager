package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class ColumnFillSpaceCenterDenseStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getVerticalDifference(abstractLayouter) / 2;

        for (Item item : row) {
            Rect childRect = item.getViewRect();
            childRect.top += difference;
            childRect.bottom += difference;
        }
    }
}

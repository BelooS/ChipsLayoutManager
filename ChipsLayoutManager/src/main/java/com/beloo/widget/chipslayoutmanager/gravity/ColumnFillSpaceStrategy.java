package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class ColumnFillSpaceStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        if (abstractLayouter.getRowSize() == 1) return;
        int difference = GravityUtil.getVerticalDifference(abstractLayouter) / (abstractLayouter.getRowSize() - 1);
        int offsetDifference = 0;

        for (Item item : row) {
            Rect childRect = item.getViewRect();

            if (childRect.top == abstractLayouter.getCanvasTopBorder()) {
                //highest view of row

                int topDif = childRect.top - abstractLayouter.getCanvasTopBorder();
                //press view to top border
                childRect.top = abstractLayouter.getCanvasTopBorder();
                childRect.bottom -= topDif;
                continue;
            }
            offsetDifference += difference;

            childRect.top += offsetDifference;
            childRect.bottom += offsetDifference;
        }

    }
}

package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class RTLRowFillSpaceStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        if (abstractLayouter.getRowSize() == 1) return;
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter) / (abstractLayouter.getRowSize() - 1);
        int offsetDifference = 0;

        for (Item item : row) {
            Rect childRect = item.getViewRect();

            if (childRect.right == abstractLayouter.getCanvasRightBorder()) {
                //right view of row

                int rightDif = abstractLayouter.getCanvasRightBorder() - childRect.right;
                //press view to right border
                childRect.left += rightDif;
                childRect.right = abstractLayouter.getCanvasRightBorder();
                continue;
            }
            offsetDifference += difference;

            childRect.right -= offsetDifference;
            childRect.left -= offsetDifference;
        }

    }
}

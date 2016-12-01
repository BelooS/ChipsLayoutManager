package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class RTLRowFillStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter) / abstractLayouter.getRowSize();
        int offsetDifference = difference;

        for (Item item : row) {
            Rect childRect = item.getViewRect();

            if (childRect.right == abstractLayouter.getCanvasRightBorder()) {
                //right view of row

                int rightDif = abstractLayouter.getCanvasRightBorder() - childRect.right;
                //press view to right border
                childRect.left += rightDif;
                childRect.right = abstractLayouter.getCanvasRightBorder();
                childRect.left -= offsetDifference;
                continue;
            }

            childRect.right -= offsetDifference;
            offsetDifference += difference;
            childRect.left -= offsetDifference;
        }

    }
}

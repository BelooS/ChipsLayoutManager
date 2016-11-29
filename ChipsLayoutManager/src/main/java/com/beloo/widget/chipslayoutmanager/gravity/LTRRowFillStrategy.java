package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

public class LTRRowFillStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int offsetDifference = GravityUtil.getHorizontalDifference(abstractLayouter);

        for (Item item : row) {

            Rect childRect = item.getViewRect();

            if (childRect.left == abstractLayouter.getCanvasLeftBorder()) {
                //left view of row

                int leftDif = childRect.left - abstractLayouter.getCanvasLeftBorder();
                //press view to left border
                childRect.left = abstractLayouter.getCanvasLeftBorder();
                childRect.right -= leftDif;

                //increase view width from right
                childRect.right += offsetDifference;
                continue;
            }

            childRect.left += offsetDifference;
            offsetDifference += offsetDifference;
            childRect.right += offsetDifference;
        }

    }
}

package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class ColumnFillGravityModifier  {

    public void modifyChildRect(AbstractLayouter abstractLayouter, Rect childRect) {

        int difference = GravityUtil.getVerticalDifference(abstractLayouter);

        if (childRect.top == abstractLayouter.getCanvasTopBorder()) {
            //highest view of row

            int topDif = childRect.top - abstractLayouter.getCanvasTopBorder();
            //press view to top border
            childRect.top = abstractLayouter.getCanvasTopBorder();
            childRect.bottom -= topDif;

            //increase view height from bottom
            childRect.bottom += difference;
            return;
        }

        if (childRect.bottom == abstractLayouter.getCanvasTopBorder() + abstractLayouter.getRowLength()) {
            //lowest view of row

            int bottomDif = abstractLayouter.getCanvasBottomBorder() - childRect.bottom;
            //press view to bottom border
            childRect.top += bottomDif;
            childRect.bottom = abstractLayouter.getCanvasBottomBorder();
            return;
        }

        //split whole difference to top/bottom differences
//        difference /= 2;

        childRect.bottom += difference;
    }
}

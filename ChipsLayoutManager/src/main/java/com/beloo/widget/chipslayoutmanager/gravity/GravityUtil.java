package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

abstract class GravityUtil {

    static int getHorizontalDifference(AbstractLayouter layouter) {
        if (layouter.getRowSize() == 1) return 0;
        return (layouter.getCanvasRightBorder() - layouter.getCanvasLeftBorder() - layouter.getRowLength()) / (layouter.getRowSize() - 1);
    }

    static int getVerticalDifference(AbstractLayouter layouter) {
        if (layouter.getRowSize() == 1) return 0;
        return (layouter.getCanvasBottomBorder() - layouter.getCanvasTopBorder() - layouter.getRowLength()) / (layouter.getRowSize() - 1);
    }

}

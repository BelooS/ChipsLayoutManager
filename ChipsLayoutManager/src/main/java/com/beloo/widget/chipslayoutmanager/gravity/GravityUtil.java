package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

abstract class GravityUtil {

    static int getParallelDifference(AbstractLayouter layouter) {
        return (layouter.getCanvasRightBorder() - layouter.getCanvasLeftBorder() - layouter.getRowLength()) / layouter.getRowSize();
    }

}

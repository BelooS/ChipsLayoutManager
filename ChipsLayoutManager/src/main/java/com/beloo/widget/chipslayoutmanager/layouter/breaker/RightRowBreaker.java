package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class RightRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewTop() > al.getCanvasTopBorder()
                && al.getViewTop() + al.getCurrentViewHeight() > al.getCanvasBottomBorder();
    }
}

package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

/** this is basis row breaker for {@link com.beloo.widget.chipslayoutmanager.layouter.LTRDownLayouter} */
class LTRDownRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getLeftOffset() > al.getCanvasLeftBorder()
                && al.getLeftOffset() + al.getCurrentViewWidth() > al.getCanvasRightBorder();
    }
}

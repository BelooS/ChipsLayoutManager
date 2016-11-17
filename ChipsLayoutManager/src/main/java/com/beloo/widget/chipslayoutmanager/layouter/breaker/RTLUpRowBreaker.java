package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

/** this is basis row breaker for {@link com.beloo.widget.chipslayoutmanager.layouter.RTLUpLayouter} */
class RTLUpRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getLeftOffset() + al.getCurrentViewWidth() > al.getCanvasRightBorder()
                && al.getLeftOffset() > al.getCanvasLeftBorder();
    }
}

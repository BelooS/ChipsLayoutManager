package com.beloo.widget.chipslayoutmanager.layouter.breaker;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

/** this is basis row breaker for {@link com.beloo.widget.chipslayoutmanager.layouter.RTLDownLayouter} */
class RTLDownRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getRightOffset() < al.getCanvasRightBorder()
                && al.getRightOffset() - al.getCurrentViewWidth() < al.getCanvasLeftBorder();

    }
}

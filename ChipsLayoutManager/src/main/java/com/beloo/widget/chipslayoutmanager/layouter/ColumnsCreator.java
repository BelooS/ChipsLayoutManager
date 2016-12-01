package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

class ColumnsCreator implements ILayouterCreator {

    private RecyclerView.LayoutManager layoutManager;

    ColumnsCreator(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public AbstractLayouter.Builder createBackwardBuilder() {
        return LeftLayouter.newBuilder();
    }

    @Override
    public AbstractLayouter.Builder createForwardBuilder() {
        return RightLayouter.newBuilder();
    }

    @Override
    public Rect createOffsetRectForBackwardLayouter(Rect anchorRect) {
        return new Rect(
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                0,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                //we shouldn't include anchor view here, so anchorTop is a bottomOffset
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top);
    }

    @Override
    public Rect createOffsetRectForForwardLayouter(Rect anchorRect) {
        return new Rect(
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                //we should include anchor view here, so anchorTop is a topOffset
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                0);
    }
}

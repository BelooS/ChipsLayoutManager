package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

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
    public Rect createOffsetRectForBackwardLayouter(@NonNull AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();

        return new Rect(
                anchorRect == null ? 0 : anchorRect.left,
                0,
                anchorRect == null ? 0 : anchorRect.right,
                //we shouldn't include anchor view here, so anchorTop is a bottomOffset
                anchorRect == null ? 0 : anchorRect.top);
    }

    @Override
    public Rect createOffsetRectForForwardLayouter(@NonNull AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();

        return new Rect(
                anchorRect == null ? anchor.getPosition() == 0 ? layoutManager.getPaddingLeft() : 0 : anchorRect.left,
                //we should include anchor view here, so anchorTop is a topOffset
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? anchor.getPosition() == 0 ? layoutManager.getPaddingRight() : 0 : anchorRect.right,
                0);
    }
}

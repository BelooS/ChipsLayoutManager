package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class LTRUpLayouter extends AbstractLayouter implements ILayouter {

    protected int viewRight;

    LTRUpLayouter(ChipsLayoutManager layoutManager, IChildGravityResolver childGravityResolver, int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset, childGravityResolver);
        this.viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        viewTop = layoutRow(rowViews, viewTop, viewBottom, viewRight);

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewBottom = viewTop;
    }

    @Override
    void addView(View view) {
        layoutManager.addView(view, 0);
    }

    @Override
    Rect createViewRect(View view) {
        int left = viewRight - currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(left, viewTop, viewRight, viewBottom);
        viewRight = left;
        return viewRect;
    }

    @Override
    public void onAttachView(View view) {
        super.onAttachView(view);

        if (viewRight != getCanvasWidth() && viewRight - layoutManager.getDecoratedMeasuredWidth(view) < 0) {
            //new row
            viewRight = getCanvasWidth();
            viewBottom = viewTop;
        } else {
            viewRight = layoutManager.getDecoratedLeft(view);
        }

        viewTop = Math.min(viewTop, layoutManager.getDecoratedTop(view));
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewBottom < 0;
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        int bufLeft = viewRight - currentViewWidth;
        return bufLeft < 0 && viewRight < getCanvasWidth();
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new DecrementalPositionIterator();
    }

}

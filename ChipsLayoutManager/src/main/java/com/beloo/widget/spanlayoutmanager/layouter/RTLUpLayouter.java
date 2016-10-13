package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class RTLUpLayouter extends AbstractLayouter implements ILayouter {
    private static final String TAG = RTLUpLayouter.class.getSimpleName();

    protected int viewLeft;

    RTLUpLayouter(ChipsLayoutManager spanLayoutManager, IChildGravityResolver childGravityResolver, int topOffset, int leftOffset, int bottomOffset) {
        super(spanLayoutManager, topOffset, bottomOffset, childGravityResolver);
        Log.d(TAG, "start bottom offset = " + bottomOffset);
        this.viewLeft = leftOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        Log.d(TAG, "row bottom " + viewBottom);
        Log.d(TAG, "row top " + viewTop);
        viewTop = layoutRow(rowViews, viewTop, viewBottom, -(getCanvasWidth() - viewLeft));

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewBottom = viewTop;
    }

    @Override
    void addView(View view) {
        layoutManager.addView(view, 0);
    }

    @Override
    Rect createViewRect(View view) {
        int right = viewLeft + currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(viewLeft, viewTop, right, viewBottom);
        viewLeft = right;
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {

        if (viewLeft != 0 && viewLeft + layoutManager.getDecoratedMeasuredWidth(view) > getCanvasWidth()) {
            viewLeft = 0;
            viewBottom = viewTop;
        } else {
            viewLeft = layoutManager.getDecoratedRight(view);
        }

        viewTop = Math.min(viewTop, layoutManager.getDecoratedTop(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewBottom < 0;
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        int bufRight = viewLeft + currentViewWidth;
        return bufRight > getCanvasWidth() && viewLeft > 0;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new DecrementalPositionIterator();
    }

}

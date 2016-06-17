package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.SpanLayoutManager;

class RTLUpLayouter extends AbstractLayouter implements ILayouter {
    private static final String TAG = RTLUpLayouter.class.getSimpleName();

    protected int viewLeft;

    RTLUpLayouter(SpanLayoutManager spanLayoutManager, int topOffset, int leftOffset, int bottomOffset) {
        super(spanLayoutManager, topOffset, bottomOffset);
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
        viewTop = layoutManager.layoutRow(rowViews, viewTop, viewBottom, -(getCanvasWidth() - viewLeft), true);

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewBottom = viewTop;
    }

    public void placeView(View view) {

        /* view can be placed in current row, but we can't determine real position, until row will be filled,
        so generate rect for the view and layout it in the end of the row
         */

        int right = viewLeft + currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(viewLeft, viewTop, right, viewBottom);
        viewLeft = right;

        rowViews.add(new Pair<>(viewRect, view));
    }

    @Override
    public void onAttachView(View view) {
        super.onAttachView(view);

        if (viewLeft != 0 && viewLeft + layoutManager.getDecoratedMeasuredWidth(view) > getCanvasWidth()) {
            viewLeft = 0;
            viewBottom = viewTop;
        } else {
            viewLeft = layoutManager.getDecoratedRight(view);
        }

        viewTop = Math.min(viewTop, layoutManager.getDecoratedTop(view));

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

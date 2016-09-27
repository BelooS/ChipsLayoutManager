package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.SpanLayoutManager;
import com.beloo.widget.spanlayoutmanager.layouter.position_iterator.AbstractPositionIterator;

class LTRUpLayouter extends AbstractLayouter implements ILayouter {

    private int viewRight;

    LTRUpLayouter(SpanLayoutManager layoutManager, int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset);
        this.viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        viewTop = layoutManager.layoutRow(rowViews, viewTop, viewBottom, viewRight, true);

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewBottom = viewTop;
    }

    public void placeView(View view) {

        /* view can be placed in current row, but we can't determine real position, until row will be filled,
        so generate rect for the view and layout it in the end of the row
         */

        int left = viewRight - currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(left, viewTop, viewRight, viewBottom);
        viewRight = left;

        rowViews.add(new Pair<>(viewRect, view));
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
        return positionIteratorFactory.getDecrementalPositionIterator(layoutManager.getItemCount());
    }

}

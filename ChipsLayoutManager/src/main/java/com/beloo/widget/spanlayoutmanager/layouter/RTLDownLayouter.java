package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;

class RTLDownLayouter extends AbstractLayouter {

    private int maxBottom;
    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager, int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset);
        viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutManager.layoutRow(rowViews, viewTop, maxBottom, 0, false);

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewTop = maxBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return viewRight < getCanvasWidth() && viewRight - currentViewWidth < 0;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(layoutManager.getItemCount());
    }

    @Override
    public void placeView(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, viewTop, viewRight, viewTop + currentViewHeight);
        rowViews.add(new Pair<>(viewRect, view));

        viewRight = viewRect.left;
        maxBottom = Math.max(maxBottom, viewRect.bottom);
    }

    @Override
    public void onAttachView(View view) {
        super.onAttachView(view);
        viewTop = layoutManager.getDecoratedTop(view);
        viewRight = layoutManager.getDecoratedLeft(view);

        maxBottom = Math.max(maxBottom, layoutManager.getDecoratedBottom(view));
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}

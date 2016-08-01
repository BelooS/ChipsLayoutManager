package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.SpanLayoutManager;

class LTRDownLayouter extends AbstractLayouter {

    private int maxBottom;
    private int viewLeft;

    LTRDownLayouter(SpanLayoutManager layoutManager, int topOffset, int leftOffset, int bottomOffset) {
        super(layoutManager, topOffset, bottomOffset);
        viewLeft = leftOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutManager.layoutRow(rowViews, viewTop, maxBottom, 0, false);

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewTop = maxBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return viewLeft > 0 && viewLeft + currentViewWidth > getCanvasWidth();
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(layoutManager.getItemCount());
    }

    @Override
    public void placeView(View view) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + currentViewWidth, viewTop + currentViewHeight);
        rowViews.add(new Pair<>(viewRect, view));

        viewLeft = viewRect.right;
        maxBottom = Math.max(maxBottom, viewRect.bottom);
    }

    @Override
    public void onAttachView(View view) {
        super.onAttachView(view);
        viewTop = layoutManager.getDecoratedTop(view);
        viewLeft = layoutManager.getDecoratedRight(view);
        maxBottom = Math.max(maxBottom, layoutManager.getDecoratedBottom(view));
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}

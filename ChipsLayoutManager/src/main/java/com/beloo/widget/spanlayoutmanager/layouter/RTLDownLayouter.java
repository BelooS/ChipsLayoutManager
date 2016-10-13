package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class RTLDownLayouter extends AbstractLayouter {

    private int maxBottom;
    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager, IChildGravityResolver childGravityResolver, int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset, childGravityResolver);
        viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutRow(rowViews, viewTop, maxBottom, 0);

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewTop = maxBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    void addView(View view) {
        layoutManager.addView(view, 0);
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
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, viewTop, viewRight, viewTop + currentViewHeight);
        viewRight = viewRect.left;
        maxBottom = Math.max(maxBottom, viewRect.bottom);
        return viewRect;
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

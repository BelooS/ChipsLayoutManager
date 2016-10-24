package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class LTRUpLayouter extends AbstractLayouter implements ILayouter {

    private int viewRight;

    LTRUpLayouter(ChipsLayoutManager layoutManager,
                  IChildGravityResolver childGravityResolver,
                  IViewCacheStorage cacheStorage,
                  int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        this.viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        int leftOffsetOfRow = viewRight;
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;

            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            rowTop = Math.min(rowTop, viewRect.top);
            rowBottom = Math.max(rowBottom, viewRect.bottom);
        }

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        layoutRow(rowViews, rowTop, rowBottom);

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowBottom = rowTop;
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view, 0);
    }

    @Override
    Rect createViewRect(View view) {
        int left = viewRight - currentViewWidth;
        int viewTop = rowBottom - currentViewHeight;

        Rect viewRect = new Rect(left, viewTop, viewRight, rowBottom);
        viewRight = viewRect.left;
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {

        if (viewRight != getCanvasRightBorder() && viewRight - getLayoutManager().getDecoratedMeasuredWidth(view) < getCanvasLeftBorder()) {
            //new row
            viewRight = getCanvasRightBorder();
            rowBottom = rowTop;
        } else {
            viewRight = getLayoutManager().getDecoratedLeft(view);
        }

        rowTop = Math.min(rowTop, getLayoutManager().getDecoratedTop(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return rowBottom < getCanvasTopBorder();
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        //when go up, check cache to layout according previous down algorithm
        boolean stopDueToCache = getCacheStorage().isPositionEndsRow(getCurrentViewPosition());
        if (stopDueToCache) return true;

        int bufLeft = viewRight - currentViewWidth;
        return super.canNotBePlacedInCurrentRow() || (bufLeft < getCanvasLeftBorder() && viewRight < getCanvasRightBorder());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new DecrementalPositionIterator();
    }

}

package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;

class LTRUpLayouter extends AbstractLayouter implements ILayouter {

    private int viewRight;

    LTRUpLayouter(ChipsLayoutManager layoutManager,
                  IChildGravityResolver childGravityResolver,
                  IViewCacheStorage cacheStorage,
                  Rect offsetRect) {
        super(layoutManager, offsetRect, cacheStorage, childGravityResolver);
        this.viewRight = offsetRect.right;
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view, 0);
    }

    @Override
    void onPreLayout() {
        int leftOffsetOfRow = viewRight - getCanvasLeftBorder();
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;

            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            rowTop = Math.min(rowTop, viewRect.top);
            rowBottom = Math.max(rowBottom, viewRect.bottom);
        }
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowBottom = rowTop;
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

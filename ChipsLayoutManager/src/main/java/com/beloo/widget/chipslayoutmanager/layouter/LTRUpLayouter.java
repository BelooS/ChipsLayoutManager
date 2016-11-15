package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import java.util.Collections;

class LTRUpLayouter extends AbstractLayouter implements ILayouter {

    private LTRUpLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
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
    boolean isAttachedViewFromNewRow(View view) {
        int bottomOfCurrentView = getLayoutManager().getDecoratedBottom(view);
        int rightOfCurrentView = getLayoutManager().getDecoratedRight(view);

        return rowTop >= bottomOfCurrentView
                && rightOfCurrentView > viewRight;
    }

    @Override
    Rect createViewRect(View view) {
        int left = viewRight - getCurrentViewWidth();
        int viewTop = rowBottom - getCurrentViewHeight();

        Rect viewRect = new Rect(left, viewTop, viewRight, rowBottom);
        viewRight = viewRect.left;
        return viewRect;
    }

    @Override
    public void onInterceptAttachView(View view) {
        if (viewRight != getCanvasRightBorder() && viewRight - getCurrentViewWidth() < getCanvasLeftBorder()) {
            //new row
            viewRight = getCanvasRightBorder();
            rowBottom = rowTop;
        } else {
            viewRight = getLayoutManager().getDecoratedLeft(view);
        }

        rowTop = Math.min(rowTop, getLayoutManager().getDecoratedTop(view));
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        //when go up, check cache to layout according previous down algorithm
        boolean stopDueToCache = getCacheStorage().isPositionEndsRow(getCurrentViewPosition());
        if (stopDueToCache) return true;

        int bufLeft = viewRight - getCurrentViewWidth();
        return super.canNotBePlacedInCurrentRow()
                || (getBreaker().isItemBreakRow(getCurrentViewPosition()))
                || (bufLeft < getCanvasLeftBorder() && viewRight < getCanvasRightBorder());
    }

    @Override
    AbstractPositionIterator createPositionIterator() {
        return new DecrementalPositionIterator();
    }


    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public LTRUpLayouter build() {
            return new LTRUpLayouter(this);
        }
    }
}

package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

class RTLUpLayouter extends AbstractLayouter implements ILayouter {
    private static final String TAG = RTLUpLayouter.class.getSimpleName();

    private RTLUpLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    void onPreLayout() {
        int leftOffsetOfRow = -(getCanvasRightBorder() - viewLeft);

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
        viewLeft = getCanvasLeftBorder();
        rowBottom = rowTop;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {
        int bottomOfCurrentView = getLayoutManager().getDecoratedBottom(view);
        int leftOfCurrentView = getLayoutManager().getDecoratedLeft(view);

        return rowTop >= bottomOfCurrentView
                && leftOfCurrentView < viewLeft;
    }

    @Override
    Rect createViewRect(View view) {
        int right = viewLeft + getCurrentViewWidth();
        int viewTop = rowBottom - getCurrentViewHeight();
        Rect viewRect = new Rect(viewLeft, viewTop, right, rowBottom);
        viewLeft = viewRect.right;
        return viewRect;
    }

    @Override
    public void onInterceptAttachView(View view) {
        if (viewLeft != getCanvasLeftBorder() && viewLeft + getCurrentViewWidth() > getCanvasRightBorder()) {
            viewLeft = getCanvasLeftBorder();
            rowBottom = rowTop;
        } else {
            viewLeft = getLayoutManager().getDecoratedRight(view);
        }

        rowTop = Math.min(rowTop, getLayoutManager().getDecoratedTop(view));
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        //when go up, check cache to layout according previous down algorithm
        boolean stopDueToCache = getCacheStorage().isPositionEndsRow(getCurrentViewPosition());
        return stopDueToCache
                || super.canNotBePlacedInCurrentRow()
                || (getBreaker().isItemBreakRow(getCurrentViewPosition()))
                || (viewLeft + getCurrentViewWidth() > getCanvasRightBorder() && viewLeft > getCanvasLeftBorder());

    }

    @Override
    AbstractPositionIterator createPositionIterator() {
        return new DecrementalPositionIterator();
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public RTLUpLayouter build() {
            return new RTLUpLayouter(this);
        }
    }
}

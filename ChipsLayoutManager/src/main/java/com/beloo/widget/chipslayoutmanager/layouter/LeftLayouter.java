package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

class LeftLayouter extends AbstractLayouter {

    private LeftLayouter(Builder builder) {
        super(builder);
    }

    @Override
    Rect createViewRect(View view) {

        int left = viewRight - getCurrentViewWidth();
        int viewTop = viewBottom - getCurrentViewHeight();

        Rect viewRect = new Rect(left, viewTop, viewRight, viewBottom);

        viewBottom = viewRect.top;

        return viewRect;
    }

    @Override
    void onPreLayout() {
        int topOffsetOfRow = viewBottom - getCanvasTopBorder();

        for (Pair<Rect, View> columnViewRectPair : rowViews) {
            Rect viewRect = columnViewRectPair.first;

            viewRect.top = viewRect.top - topOffsetOfRow;
            viewRect.bottom = viewRect.bottom - topOffsetOfRow;

            viewLeft = Math.min(viewLeft, viewRect.left);
            viewRight = Math.max(viewBottom, viewRect.bottom);
        }
    }

    @Override
    void onAfterLayout() {
        viewBottom = getCanvasBottomBorder();
        viewRight = viewLeft;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {
        int bottomOfCurrentView = getLayoutManager().getDecoratedBottom(view);
        int rightOfCurrentView = getLayoutManager().getDecoratedRight(view);

        return viewLeft >= rightOfCurrentView
                && bottomOfCurrentView > viewBottom;
    }

    @Override
    AbstractPositionIterator createPositionIterator() {
        return new DecrementalPositionIterator();
    }

    @Override
    void onInterceptAttachView(View view) {
        if (viewBottom != getCanvasBottomBorder() && viewBottom - getCurrentViewHeight() < getCanvasTopBorder()) {
            //new column
            viewBottom = getCanvasBottomBorder();
            viewRight = viewLeft;
        } else {
            viewBottom = getLayoutManager().getDecoratedTop(view);
        }

        viewLeft = Math.min(viewLeft, getLayoutManager().getDecoratedLeft(view));
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public LeftLayouter build() {
            return new LeftLayouter(this);
        }
    }
}

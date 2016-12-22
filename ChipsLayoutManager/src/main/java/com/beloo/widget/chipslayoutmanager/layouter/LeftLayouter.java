package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import java.util.Collections;

class LeftLayouter extends AbstractLayouter {

    private LeftLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
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
    boolean isReverseOrder() {
        return true;
    }

    @Override
    void onPreLayout() {
        int topOffsetOfRow = viewBottom - getCanvasTopBorder();
        viewBottom = 0;

        for (Pair<Rect, View> columnViewRectPair : rowViews) {
            Rect viewRect = columnViewRectPair.first;

            viewRect.top = viewRect.top - topOffsetOfRow;
            viewRect.bottom = viewRect.bottom - topOffsetOfRow;

            viewBottom = Math.max(viewBottom, viewRect.bottom);
            viewLeft = Math.min(viewLeft, viewRect.left);
            viewRight = Math.max(viewRight, viewRect.right);
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

    @Override
    public int getStartRowBorder() {
        return getViewLeft();
    }

    @Override
    public int getEndRowBorder() {
        return getViewRight();
    }

    @Override
    public int getRowLength() {
        return viewBottom - getCanvasTopBorder();
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public LeftLayouter createLayouter() {
            return new LeftLayouter(this);
        }
    }
}

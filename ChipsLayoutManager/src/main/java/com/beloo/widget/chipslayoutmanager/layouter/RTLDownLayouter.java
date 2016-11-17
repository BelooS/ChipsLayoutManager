package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

class RTLDownLayouter extends AbstractLayouter {

    private boolean isPurged;

    private RTLDownLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    void onPreLayout() {
        if (!rowViews.isEmpty()) {
            //todo this isn't great place for that. Should be refactored somehow
            if (!isPurged) {
                isPurged = true;
                getCacheStorage().purgeCacheFromPosition(getLayoutManager().getPosition(rowViews.get(0).second));
            }

            getCacheStorage().storeRow(rowViews);
        }
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowTop = rowBottom;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {

        int topOfCurrentView = getLayoutManager().getDecoratedTop(view);
        int rightOfCurrentVIew = getLayoutManager().getDecoratedRight(view);

        return rowBottom <= topOfCurrentView
                && rightOfCurrentVIew > viewRight;
    }

    @Override
    AbstractPositionIterator createPositionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - getCurrentViewWidth(), rowTop, viewRight, rowTop + getCurrentViewHeight());
        viewRight = viewRect.left;
        rowBottom = Math.max(rowBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public void onInterceptAttachView(View view) {
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));
    }


    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public RTLDownLayouter build() {
            return new RTLDownLayouter(this);
        }
    }
}

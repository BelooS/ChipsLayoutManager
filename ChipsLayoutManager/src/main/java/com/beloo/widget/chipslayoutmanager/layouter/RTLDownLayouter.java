package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

class RTLDownLayouter extends AbstractLayouter {

    private RTLDownLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    void onPreLayout() {
        getCacheStorage().storeRow(rowViews);
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
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow()
                || (getCurrentViewPosition() != 0 && getBreaker().isItemBreakRow(getCurrentViewPosition() - 1))
                || (viewRight < getCanvasRightBorder() && viewRight - getCurrentViewWidth() < getCanvasLeftBorder());
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
    public boolean onAttachView(View view) {
        boolean isViewAttached = super.onAttachView(view);
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));

        return isViewAttached;
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

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
        viewTop = viewBottom;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {

        int topOfCurrentView = getLayoutManager().getDecoratedTop(view);
        int rightOfCurrentVIew = getLayoutManager().getDecoratedRight(view);

        return viewBottom <= topOfCurrentView
                && rightOfCurrentVIew > viewRight;
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - getCurrentViewWidth(), viewTop, viewRight, viewTop + getCurrentViewHeight());
        viewRight = viewRect.left;
        viewBottom = Math.max(viewBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    boolean isReverseOrder() {
        return false;
    }

    @Override
    public void onInterceptAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        viewBottom = Math.max(viewBottom, getLayoutManager().getDecoratedBottom(view));
    }

    @Override
    public int getStartRowBorder() {
        return getViewTop();
    }

    @Override
    public int getEndRowBorder() {
        return getViewBottom();
    }

    @Override
    public int getRowLength() {
        return getCanvasRightBorder() - viewRight;
    }


    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public RTLDownLayouter createLayouter() {
            return new RTLDownLayouter(this);
        }
    }
}

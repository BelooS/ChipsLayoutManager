package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

class RightLayouter extends AbstractLayouter {

    private boolean isPurged;

    private RightLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + getCurrentViewWidth(), viewTop + getCurrentViewHeight());

        viewBottom = viewRect.bottom;
        viewTop = viewBottom;
        viewRight = Math.max(viewRight, viewRect.right);
        return viewRect;
    }

    @Override
    boolean isReverseOrder() {
        return false;
    }

    @Override
    void onPreLayout() {
        if (!rowViews.isEmpty()) {
            //todo this isn't great place for that. Should be refactored somehow
            if (!isPurged) {
                isPurged = true;
                getCacheStorage().purgeCacheFromPosition(getLayoutManager().getPosition(rowViews.get(0).second));
            }

            //cache only when go down
            getCacheStorage().storeRow(rowViews);
        }
    }

    @Override
    void onAfterLayout() {
        //go to next column, increase left coordinate, reset top
        viewLeft = getViewRight();
        viewTop = getCanvasTopBorder();
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {
        int leftOfCurrentView = getLayoutManager().getDecoratedLeft(view);
        int topOfCurrentView = getLayoutManager().getDecoratedTop(view);

        return viewRight <= leftOfCurrentView
                && topOfCurrentView < viewTop;
    }

    @Override
    void onInterceptAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedBottom(view);
        viewLeft = getLayoutManager().getDecoratedLeft(view);
        viewRight = Math.max(viewRight, getLayoutManager().getDecoratedRight(view));
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
        return viewTop - getCanvasTopBorder();
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public RightLayouter createLayouter() {
            return new RightLayouter(this);
        }
    }
}

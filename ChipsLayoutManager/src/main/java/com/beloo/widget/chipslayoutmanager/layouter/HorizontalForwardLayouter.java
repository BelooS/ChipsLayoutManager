package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

class HorizontalForwardLayouter extends AbstractLayouter {

    private boolean isPurged;

    private HorizontalForwardLayouter(Builder builder) {
        super(builder);
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + getCurrentViewWidth(), viewTop + getCurrentViewHeight());

        viewTop = viewRect.bottom;
        viewRight = Math.max(viewRight, viewRect.right);
        return viewRect;
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
    AbstractPositionIterator createPositionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    void onInterceptAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedBottom(view);
        viewLeft = getLayoutManager().getDecoratedLeft(view);
        viewRight = Math.max(viewRight, getLayoutManager().getDecoratedRight(view));
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public HorizontalForwardLayouter build() {
            return new HorizontalForwardLayouter(this);
        }
    }
}

package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

class LTRDownLayouter extends AbstractLayouter {

    private boolean isPurged;

    private LTRDownLayouter(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + getCurrentViewWidth(), viewTop + getCurrentViewHeight());

        viewLeft = viewRect.right;
        viewBottom = Math.max(viewBottom, viewRect.bottom);
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
        //go to next row, increase top coordinate, reset left
        viewLeft = getCanvasLeftBorder();
        viewTop = viewBottom;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {

        int topOfCurrentView = getLayoutManager().getDecoratedTop(view);
        int leftOfCurrentView = getLayoutManager().getDecoratedLeft(view);

        return viewBottom <= topOfCurrentView
                && leftOfCurrentView < viewLeft;

    }

    @Override
    public void onInterceptAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedTop(view);
        viewLeft = getLayoutManager().getDecoratedRight(view);
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
        return viewLeft - getCanvasLeftBorder();
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public LTRDownLayouter createLayouter() {
            return new LTRDownLayouter(this);
        }
    }
}

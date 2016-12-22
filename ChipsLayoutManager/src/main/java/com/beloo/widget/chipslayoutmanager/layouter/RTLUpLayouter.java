package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import java.util.Collections;

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

        viewLeft = rowViews.size() > 0 ? Integer.MAX_VALUE : 0;

        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;

            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            viewLeft = Math.min(viewLeft, viewRect.left);
            viewTop = Math.min(viewTop, viewRect.top);
            viewBottom = Math.max(viewBottom, viewRect.bottom);
        }
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewLeft = getCanvasLeftBorder();
        viewBottom = viewTop;
    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {
        int bottomOfCurrentView = getLayoutManager().getDecoratedBottom(view);
        int leftOfCurrentView = getLayoutManager().getDecoratedLeft(view);

        return viewTop >= bottomOfCurrentView
                && leftOfCurrentView < viewLeft;
    }

    @Override
    Rect createViewRect(View view) {
        int right = viewLeft + getCurrentViewWidth();
        int viewTop = viewBottom - getCurrentViewHeight();
        Rect viewRect = new Rect(viewLeft, viewTop, right, viewBottom);
        viewLeft = viewRect.right;
        return viewRect;
    }

    @Override
    boolean isReverseOrder() {
        return true;
    }

    @Override
    public void onInterceptAttachView(View view) {
        if (viewLeft != getCanvasLeftBorder() && viewLeft + getCurrentViewWidth() > getCanvasRightBorder()) {
            viewLeft = getCanvasLeftBorder();
            viewBottom = viewTop;
        } else {
            viewLeft = getLayoutManager().getDecoratedRight(view);
        }

        viewTop = Math.min(viewTop, getLayoutManager().getDecoratedTop(view));
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
        return getCanvasRightBorder() - viewLeft;
    }

    public static final class Builder extends AbstractLayouter.Builder {
        private Builder() {
        }

        @NonNull
        public RTLUpLayouter createLayouter() {
            return new RTLUpLayouter(this);
        }
    }
}

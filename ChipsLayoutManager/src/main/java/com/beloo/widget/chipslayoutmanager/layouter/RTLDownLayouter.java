package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacer;

class RTLDownLayouter extends AbstractLayouter {

    private int viewRight;

    private RTLDownLayouter(Builder builder) {
        super(builder);
        viewRight = builder.viewRight;
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
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow() || (viewRight < getCanvasRightBorder() && viewRight - currentViewWidth < getCanvasLeftBorder());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, rowTop, viewRight, rowTop + currentViewHeight);
        viewRight = viewRect.left;
        rowBottom = Math.max(rowBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));

        return super.onAttachView(view);
    }


    public static final class Builder extends AbstractLayouter.Builder {
        private int viewRight;

        private Builder() {
        }

        @NonNull
        @Override
        public AbstractLayouter.Builder offsetRect(@NonNull Rect offsetRect) {
            viewRight = offsetRect.right;
            return super.offsetRect(offsetRect);
        }

        @NonNull
        public RTLDownLayouter build() {
            return new RTLDownLayouter(this);
        }
    }
}

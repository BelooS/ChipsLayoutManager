package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;

class LTRDownLayouter extends AbstractLayouter {

    private int viewLeft;

    LTRDownLayouter(ChipsLayoutManager layoutManager,
                    IChildGravityResolver childGravityResolver,
                    IViewCacheStorage cacheStorage,
                    Rect offsetRect) {
        super(layoutManager, offsetRect, cacheStorage, childGravityResolver);
        viewLeft = offsetRect.left;
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view);
    }

    @Override
    void onPreLayout() {
        //cache only when go down
        getCacheStorage().storeRow(rowViews);
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewLeft = getCanvasLeftBorder();
        rowTop = rowBottom;
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow() || (viewLeft > getCanvasLeftBorder() && viewLeft + currentViewWidth > getCanvasRightBorder());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewLeft, rowTop, viewLeft + currentViewWidth, rowTop + currentViewHeight);

        viewLeft = viewRect.right;
        rowBottom = Math.max(rowBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewLeft = getLayoutManager().getDecoratedRight(view);
        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return rowTop > getCanvasBottomBorder();
    }

}

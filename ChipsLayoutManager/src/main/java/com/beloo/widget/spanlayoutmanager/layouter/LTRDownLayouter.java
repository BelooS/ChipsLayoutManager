package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class LTRDownLayouter extends AbstractLayouter {

    private int viewLeft;

    LTRDownLayouter(ChipsLayoutManager layoutManager,
                    IChildGravityResolver childGravityResolver,
                    IViewCacheStorage cacheStorage,
                    int topOffset, int leftOffset, int bottomOffset) {
        super(layoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        viewLeft = leftOffset;
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

package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class RTLDownLayouter extends AbstractLayouter {

    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager, IChildGravityResolver childGravityResolver, IViewCacheStorage cacheStorage, int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        getCacheStorage().storeRow(rowViews);

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutRow(rowViews, rowTop, rowBottom);

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowTop = rowBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view);
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow() || (viewRight < getCanvasRightBorder() && viewRight - currentViewWidth < 0);
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

    @Override
    public boolean isFinishedLayouting() {
        return rowTop > getCanvasBottomBorder();
    }

}

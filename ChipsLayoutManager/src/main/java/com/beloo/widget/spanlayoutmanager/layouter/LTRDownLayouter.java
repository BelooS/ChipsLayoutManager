package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Pair;
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
    public void layoutRow() {
        super.layoutRow();
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutRow(rowViews, viewTop, viewBottom, 0);

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewTop = viewBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view);
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return (viewLeft > 0 && viewLeft + currentViewWidth > getCanvasWidth());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + currentViewWidth, viewTop + currentViewHeight);

        viewLeft = viewRect.right;
        viewBottom = Math.max(viewBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedTop(view);
        viewLeft = getLayoutManager().getDecoratedRight(view);
        viewBottom = Math.max(viewBottom, getLayoutManager().getDecoratedBottom(view));
        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}

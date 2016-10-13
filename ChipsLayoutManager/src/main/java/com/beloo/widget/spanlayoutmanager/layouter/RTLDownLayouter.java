package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class RTLDownLayouter extends AbstractLayouter {

    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager,
                    IChildGravityResolver childGravityResolver,
                    IViewCacheStorage cacheStorage,
                    int topOffset, int bottomOffset, int rightOffset) {
        super(layoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        viewRight = rightOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutRow(rowViews, viewTop, viewBottom, 0);

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewTop = viewBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view, 0);
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return (viewRight < getCanvasWidth() && viewRight - currentViewWidth < 0);
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    void loadFromCache(@NonNull Rect rect) {
        viewRight = rect.left;
        viewBottom = Math.max(viewBottom, rect.bottom);
        viewTop = rect.top;
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, viewTop, viewRight, viewTop + currentViewHeight);
        viewRight = viewRect.left;
        viewBottom = Math.max(viewBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        viewTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        viewBottom = Math.max(viewBottom, getLayoutManager().getDecoratedBottom(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}

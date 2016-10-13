package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

class RTLUpLayouter extends AbstractLayouter implements ILayouter {
    private static final String TAG = RTLUpLayouter.class.getSimpleName();

    protected int viewLeft;

    RTLUpLayouter(ChipsLayoutManager spanLayoutManager,
                  IChildGravityResolver childGravityResolver,
                  IViewCacheStorage cacheStorage,
                  int topOffset, int leftOffset, int bottomOffset) {
        super(spanLayoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        Log.d(TAG, "start bottom offset = " + bottomOffset);
        this.viewLeft = leftOffset;
    }

    @Override
    public void layoutRow() {
        super.layoutRow();

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        Log.d(TAG, "row bottom " + viewBottom);
        Log.d(TAG, "row top " + viewTop);
        viewTop = layoutRow(rowViews, viewTop, viewBottom, -(getCanvasWidth() - viewLeft));

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewBottom = viewTop;
    }

    @Override
    void addView(View view) {
        getLayoutManager().addView(view, 0);
    }

    @Override
    void loadFromCache(@NonNull Rect rect) {
        viewLeft = rect.right;
        viewBottom = rect.bottom;
        viewTop = rect.top;
    }

    @Override
    Rect createViewRect(View view) {
        int right = viewLeft + currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(viewLeft, viewTop, right, viewBottom);
        viewLeft = viewRect.right;
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {

        if (viewLeft != 0 && viewLeft + getLayoutManager().getDecoratedMeasuredWidth(view) > getCanvasWidth()) {
            viewLeft = 0;
            viewBottom = viewTop;
        } else {
            viewLeft = getLayoutManager().getDecoratedRight(view);
        }

        viewTop = Math.min(viewTop, getLayoutManager().getDecoratedTop(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewBottom < 0;
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        int bufRight = viewLeft + currentViewWidth;
        return bufRight > getCanvasWidth() && viewLeft > 0;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new DecrementalPositionIterator();
    }

}
